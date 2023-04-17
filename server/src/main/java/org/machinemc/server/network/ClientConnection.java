package org.machinemc.server.network;

import io.netty.channel.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.auth.Crypt;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.Machine;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.packets.out.login.PacketLoginOutDisconnect;
import org.machinemc.server.network.packets.out.login.PacketLoginOutSetCompression;
import org.machinemc.server.network.packets.out.play.PacketPlayOutDisconnect;
import org.machinemc.server.network.packets.out.play.PacketPlayOutKeepAlive;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Player connection implementation using netty.
 */
public class ClientConnection implements PlayerConnection {

    @Getter
    private final NettyServer nettyServer;
    @Getter
    private final Machine server;

    @Getter
    private @Nullable ClientState state;
    private final Channel channel;
    @Getter
    private final InetSocketAddress address;

    @Getter @Setter
    private @Nullable PublicKeyData publicKeyData;
    @Getter @Setter
    private @Nullable String loginUsername;

    @Getter
    private @Nullable ServerPlayer owner;

    @Getter
    private int compressionThreshold = -1;

    private @Nullable SecretKey secretKey;
    protected EncryptionContext encryptionContext;

    @Getter
    private long keepAliveKey = -1;
    private long keepAliveRequest;
    private long keepAliveResponse;

    public ClientConnection(final NettyServer nettyServer, final Channel channel) {
        this.nettyServer = nettyServer;
        this.channel = channel;
        this.address = (InetSocketAddress) channel.remoteAddress();
        server = nettyServer.getServer();
        setState(ClientState.HANDSHAKE);
        channel.closeFuture().addListener(future -> handleClose());
    }

    @Override
    @Synchronized
    public ChannelFuture send(final Packet packet) {
        if (!channel.isOpen())
            throw new IllegalStateException();
        if (!Packet.PacketState.out().contains(packet.getPacketState())) throw new UnsupportedOperationException();
        final ChannelFuture channelfuture = channel.writeAndFlush(packet);
        channelfuture.addListener((ChannelFutureListener) future -> {
            if (future.cause() == null) return;
            if (!future.channel().isOpen()) return;
            server.getExceptionHandler().handle(future.cause());
        });
        return channelfuture;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    /**
     * Changes state of the connection.
     * <p>
     * Can't be changed to {@link ClientState#DISCONNECTED}, use {@link #disconnect()} instead,
     * or when the client has been already disconnected.
     * @param state new state
     */
    @Synchronized
    public void setState(final ClientState state) {
        if (state == ClientState.DISCONNECTED)
            throw new UnsupportedOperationException("You can't set the connection's state to disconnected");
        if (this.state == ClientState.DISCONNECTED)
            throw new UnsupportedOperationException("Connection has been already closed");
        this.state = state;
    }

    /**
     * Changes the owner of the connection in case there isn't one, login username
     * and username of provided player instance have to match.
     * @param player new player
     */
    @Synchronized
    public void setOwner(final ServerPlayer player) {
        if (owner != null)
            throw new IllegalStateException("Connection has been already initialized");
        if (!player.getName().equals(loginUsername))
            throw new IllegalStateException("Player's name and login name has to match");
        owner = player;
    }

    /**
     * Sets the compression of this client connection.
     * @param threshold compression threshold
     * @return whether the operation was successful
     */
    public boolean setCompression(final int threshold) {
        if (state != ClientState.LOGIN) throw new UnsupportedOperationException();
        try {
            if (!send(new PacketLoginOutSetCompression(threshold)).sync().isSuccess())
                return false;
            compressionThreshold = threshold;
            return true;
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Disables the compression of this client connection.
     * @return whether the operation was successful
     */
    public boolean disableCompression() {
        return setCompression(-1);
    }

    /**
     * @return whether the client connection is compressed
     */
    public boolean isCompressed() {
        return compressionThreshold > 0;
    }

    /**
     * Starts sending the keep alive packets.
     */
    public void startKeepingAlive() {
        if (state != ClientState.PLAY)
            throw new IllegalStateException("Client isn't in the playing state");
        if (keepAliveKey != -1)
            throw new IllegalStateException("Connection is already being kept alive");

        keepAliveKey = new Random().nextLong();
        keepAliveRequest = System.currentTimeMillis();
        keepAliveResponse = System.currentTimeMillis();

        Scheduler.task(((input, session) -> {
            if (state != ClientState.PLAY) {
                session.stop();
                return null;
            }
            send(new PacketPlayOutKeepAlive(keepAliveKey));
            keepAliveRequest = System.currentTimeMillis();
            if (keepAliveRequest - keepAliveResponse > NettyServer.READ_IDLE_TIMEOUT)
                disconnect();
            return null;
        })).async()
                .repeat(true)
                .period(NettyServer.KEEP_ALIVE_FREQ)
                .run(nettyServer.getServer().getScheduler());
    }

    /**
     * Is used for responding to the keep alive packet, updates player's latency.
     */
    public void keepAlive() {
        if (state != ClientState.PLAY)
            throw new IllegalStateException("Client isn't in the playing state");
        if (keepAliveKey == -1)
            throw new IllegalStateException("Connection isn't being kept alive");
        if (owner != null) owner.setLatency((int) (System.currentTimeMillis() - keepAliveRequest));
        keepAliveResponse = System.currentTimeMillis();
    }


    @Override
    public ChannelFuture disconnect(final Component reason) {
        try {
            if (state == ClientState.LOGIN)
                this.send(new PacketLoginOutDisconnect(reason));
            if (state == ClientState.PLAY)
                this.send(new PacketPlayOutDisconnect(reason));
        } catch (Exception ignored) { }
        return close();
    }

    @Override
    @Synchronized
    public ChannelFuture close() {
        handleClose();
        return channel.close();
    }

    /**
     * Removes the connection.
     * <p>
     * Effectively handles everything that should be done when client is disconnected
     * except for closing the channel that is already expected to be closed.
     */
    private void handleClose() {
        state = ClientState.DISCONNECTED;
        nettyServer.connections.remove(this);
        if (owner != null && owner.isActive()) owner.remove();
    }

    /**
     * @return secret key used for encryption
     */
    public @Nullable SecretKey getSecretKey() {
        if (!isOpen()) throw new UnsupportedOperationException();
        return secretKey;
    }

    /**
     * Sets new secret key for encryption if there isn't one already.
     * @param key new secret key
     */
    @Synchronized
    public void setSecretKey(final SecretKey key) {
        if (!isOpen()) throw new UnsupportedOperationException();
        if (secretKey != null)
            throw new IllegalStateException("Encryption for the connection is already enabled");
        secretKey = key;
        encryptionContext = new EncryptionContext(
                Crypt.getCipher(Cipher.ENCRYPT_MODE, secretKey),
                Crypt.getCipher(Cipher.DECRYPT_MODE, secretKey)
        );
    }

    /**
     * Context containing ciphers for both encrypting and decrypting.
     * @param encrypt cipher for encryption
     * @param decrypt cipher for decryption
     */
    record EncryptionContext(Cipher encrypt, Cipher decrypt) {
    }

}
