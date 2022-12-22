package me.pesekjak.machine.network;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.auth.PublicKeyData;
import me.pesekjak.machine.entities.ServerPlayer;
import me.pesekjak.machine.network.packets.Packet;
import me.pesekjak.machine.translation.TranslatorHandler;
import me.pesekjak.machine.exception.ClientException;
import me.pesekjak.machine.network.packets.out.login.PacketLoginOutDisconnect;
import me.pesekjak.machine.network.packets.out.play.PacketPlayOutDisconnect;
import me.pesekjak.machine.network.packets.out.play.PacketPlayOutKeepAlive;
import me.pesekjak.machine.server.schedule.Scheduler;
import me.pesekjak.machine.utils.NamespacedKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

/**
 * Default player connection implementation.
 */
public class ClientConnection extends Thread implements PlayerConnection {

    private static final NamespacedKey DEFAULT_HANDLER_NAMESPACE = NamespacedKey.minecraft("default");

    @Getter
    private final @NotNull Machine server;
    @Getter
    private final @NotNull Socket clientSocket;
    @Getter
    protected @Nullable Channel channel;
    @Getter
    private @NotNull ClientState clientState = ClientState.DISCONNECTED;

    @Getter
    private long lastSendTimestamp = System.currentTimeMillis();
    @Getter
    private long lastReadTimestamp = System.currentTimeMillis();

    @Getter @Setter
    private @Nullable PublicKeyData publicKeyData;
    @Getter @Setter
    private @Nullable String loginUsername;

    @Getter
    private @Nullable ServerPlayer owner;

    @Getter
    private long keepAliveKey = -1;
    private long lastKeepAlive;

    public ClientConnection(@NotNull Machine server, @NotNull Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    /**
     * Sends packet to the client.
     * @param packet packet sent to the client
     */
    @Synchronized
    @Override
    public boolean sendPacket(@NotNull Packet packet) throws IOException {
        if(channel == null)
            throw new IllegalStateException();
        if(clientState == ClientState.DISCONNECTED)
            return false;
        if(!Packet.PacketState.out().contains(packet.getPacketState()))
            throw new UnsupportedOperationException();
        if (channel.writePacket(packet)) {
            lastSendTimestamp = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * Reads packets from the client.
     * @return packets sent by client
     */
    @Synchronized
    public Packet @Nullable [] readPackets() throws IOException {
        assert channel != null;
        if(clientState == ClientState.DISCONNECTED)
            return null;
        return channel.readPackets();
    }

    /**
     * Sets new input and output data streams of the
     * connection.
     * @param input input of the client socket
     * @param output output of the client socket
     */
    private void setChannel(@NotNull DataInputStream input, @NotNull DataOutputStream output) {
        this.channel = new Channel(this, input, output);
        this.channel.addHandlerBefore(DEFAULT_HANDLER_NAMESPACE, new PacketHandler());
    }

    /**
     * Starts the client connection.
     */
    @Override
    public void run() {
        try {
            clientState = ClientState.HANDSHAKE;
            clientSocket.setKeepAlive(true);
            clientSocket.setSoTimeout(1);
            setChannel(
                    new DataInputStream(clientSocket.getInputStream()),
                    new DataOutputStream(clientSocket.getOutputStream())
            );
            final Channel channel = getChannel();
            if(channel == null) throw new IllegalStateException();
            channel.addHandlerAfter(
                    NamespacedKey.machine("main"),
                    new TranslatorHandler(server.getTranslatorDispatcher())
            );

            int responsiveness = server.getServerResponsiveness();
            Scheduler.task(((input, session) -> {
                        if(clientState == ClientState.DISCONNECTED) {
                            session.stop();
                            return null;
                        }
                        try {
                            Packet[] packets = readPackets();
                            if(packets != null && packets.length != 0)
                                lastReadTimestamp = System.currentTimeMillis();
                        } catch (Exception exception) {
                            getServer().getExceptionHandler().handle(new ClientException(this, exception));
                            disconnect();
                            return null;
                        }
                        if(System.currentTimeMillis() - lastReadTimestamp > ServerConnectionImpl.READ_IDLE_TIMEOUT)
                            disconnect(Component.translatable("disconnect.timeout"));
                        return null;
                    }))
                    .async()
                    .repeat(true)
                    .period(responsiveness != 0 ? responsiveness : 1000 / server.getTps())
                    .run(server.getScheduler());

        } catch (Exception exception) {
            server.getExceptionHandler().handle(new ClientException(this, exception));
            close();
        }
    }

    /**
     * Closes the client connection.
     */
    @Synchronized
    @Override
    public void close() {
        clientState = ClientState.DISCONNECTED;
        server.getConnection().disconnect(this);
        try {
            if (owner != null && owner.isActive()) owner.remove();
        } catch (Exception exception) { server.getExceptionHandler().handle(exception); }
        owner = null;
        final Channel channel = getChannel();
        if(channel != null) {
            try {
                channel.close();
            } catch (Exception exception) {
                server.getExceptionHandler().handle(exception);
            }
        }
        try { clientSocket.close(); }
        catch (Exception exception) { server.getExceptionHandler().handle(exception); }
    }

    /**
     * Changes the client state of the client connection
     * @param clientState new client state
     */
    @Synchronized
    public void setClientState(ClientState clientState) {
        if(clientState == ClientState.DISCONNECTED)
            throw new UnsupportedOperationException("You can't set the connection's state to disconnected");
        if(this.clientState == ClientState.DISCONNECTED)
            throw new UnsupportedOperationException("Connection has been already closed");
        this.clientState = clientState;
    }

    /**
     * @return secret key used for encryption, only if online mode is
     * enabled.
     */
    public @Nullable SecretKey getSecretKey() {
        if(channel == null) return null;
        return channel.getSecretKey();
    }

    /**
     * Changes the secret key used for encryption, should be changed only when enabling
     * the encryption.
     * @param key new secret key
     */
    public void setSecretKey(@NotNull SecretKey key) {
        if(channel == null) return;
        channel.setSecretKey(key);
    }

    /**
     * Sets the owner of the connection, can't be changed once it's set.
     * @param player owner of the connection
     */
    public void setOwner(@NotNull ServerPlayer player) {
        if(owner != null)
            throw new IllegalStateException("Connection has been already initialized");
        if(!player.getName().equals(loginUsername))
            throw new IllegalStateException("Player's name and login name has to match");
        owner = player;
    }

    /**
     * Starts with sending the keep alive packets.
     */
    public void startKeepingAlive() {
        if(clientState != ClientState.PLAY)
            throw new IllegalStateException("Client isn't in the playing state");
        if(keepAliveKey != -1)
            throw new IllegalStateException("Connection is already being kept alive");
        keepAliveKey = new Random().nextLong();
        Scheduler.task(((input, session) -> {
                    if(clientState != ClientState.PLAY) {
                        session.stop();
                        return null;
                    }
                    try {
                        if(sendPacket(new PacketPlayOutKeepAlive(keepAliveKey)))
                            lastKeepAlive = System.currentTimeMillis();
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                    return null;
                }))
                .async()
                .repeat(true)
                .period(ServerConnectionImpl.KEEP_ALIVE_FREQ)
                .run(server.getScheduler());
    }

    /**
     * Runs when client respond with keep alive packet and updates the latency.
     */
    public void keepAlive() {
        if(clientState != ClientState.PLAY)
            throw new IllegalStateException("Client isn't in the playing state");
        if(keepAliveKey == -1)
            throw new IllegalStateException("Connection isn't being kept alive");
        if(owner != null) owner.setLatency((int) (System.currentTimeMillis() - lastKeepAlive));
    }

    /**
     * Disconnects the client from the server.
     */
    public void disconnect() {
        disconnect(Component.translatable("disconnect.disconnected"));
    }

    @Override
    public @NotNull InetSocketAddress getAddress() {
        return ((InetSocketAddress) clientSocket.getRemoteSocketAddress());
    }

    /**
     * Disconnects the client from the server with given reason that will
     * be visible in the game.
     * @param reason disconnect reason
     */
    @Override
    public void disconnect(@NotNull Component reason) {
        try {
            if(clientState == ClientState.LOGIN)
                sendPacket(new PacketLoginOutDisconnect(reason));
            if(clientState == ClientState.PLAY)
                sendPacket(new PacketPlayOutDisconnect(reason));
        } catch (Exception ignored) { }
        close();
    }

}
