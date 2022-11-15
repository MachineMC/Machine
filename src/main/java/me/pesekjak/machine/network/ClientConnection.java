package me.pesekjak.machine.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.auth.PublicKeyData;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.events.translations.TranslatorHandler;
import me.pesekjak.machine.exception.ClientException;
import me.pesekjak.machine.network.packets.Packet;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.login.PacketLoginOutDisconnect;
import me.pesekjak.machine.network.packets.out.play.PacketPlayOutDisconnect;
import me.pesekjak.machine.network.packets.out.play.PacketPlayOutKeepAlive;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.server.schedule.Scheduler;
import me.pesekjak.machine.utils.NamespacedKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ClientConnection extends Thread implements ServerProperty, AutoCloseable {

    private static final NamespacedKey DEFAULT_HANDLER_NAMESPACE = NamespacedKey.minecraft("default");

    @Getter
    private final Machine server;
    @Getter
    private final Socket clientSocket;
    @Getter
    protected Channel channel;
    @Getter
    private ClientState clientState;

    @Getter
    private long lastSendTimestamp = System.currentTimeMillis();
    @Getter
    private long lastReadTimestamp = System.currentTimeMillis();

    @Getter @Setter
    private PublicKeyData publicKeyData;
    @Getter @Setter
    private String loginUsername;

    @Getter @Nullable
    private Player owner;

    @Getter
    private long keepAliveKey = -1;
    private long lastKeepAlive;

    public ClientConnection(Machine server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    /**
     * Sends packet to the client.
     * @param packet packet sent to the client
     */
    @Synchronized
    public boolean sendPacket(PacketOut packet) throws IOException {
        assert channel != null;
        if(clientState == ClientState.DISCONNECTED)
            return false;
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
    public PacketIn[] readPackets() throws IOException {
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
    private void setChannel(DataInputStream input, DataOutputStream output) {
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
            getChannel().addHandlerAfter(
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
                            PacketIn[] packets = readPackets();
                            if(packets != null && packets.length != 0)
                                lastReadTimestamp = System.currentTimeMillis();
                        } catch (Exception exception) {
                            getServer().getExceptionHandler().handle(new ClientException(this, exception));
                            disconnect();
                            return null;
                        }
                        if(System.currentTimeMillis() - lastReadTimestamp > ServerConnection.READ_IDLE_TIMEOUT)
                            disconnect(Component.translatable("disconnect.timeout"));
                        return null;
                    }))
                    .async()
                    .repeat(true)
                    .period(responsiveness != 0 ? responsiveness : 1000 / server.getTPS())
                    .run(server.getScheduler());

        } catch (Exception exception) {
            server.getExceptionHandler().handle(new ClientException(this, exception));
            close();
        }
    }

    /**
     * Closes the client connection.
     */
    @Override
    public synchronized void close() {
        clientState = ClientState.DISCONNECTED;
        server.getConnection().disconnect(this);
        try {
            if (owner != null && owner.isActive()) owner.remove();
        } catch (Exception exception) { server.getExceptionHandler().handle(exception); }
        owner = null;
        try { channel.close(); }
        catch (Exception exception) { server.getExceptionHandler().handle(exception); }
        try { clientSocket.close(); }
        catch (Exception exception) { server.getExceptionHandler().handle(exception); }
    }

    /**
     * Changes the client state of the client connection
     * @param clientState new client state
     */
    public synchronized void setClientState(ClientState clientState) {
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
    public SecretKey getSecretKey() {
        return channel.getSecretKey();
    }

    /**
     * Changes the secret key used for encryption, should be changed only when enabling
     * the encryption.
     * @param key new secret key
     */
    public void setSecretKey(SecretKey key) {
        channel.setSecretKey(key);
    }

    /**
     * Sets the owner of the connection, can't be changed once it's set.
     * @param player owner of the connection
     */
    public void setOwner(Player player) {
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
                .period(ServerConnection.KEEP_ALIVE_FREQ)
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

    /**
     * Disconnects the client from the server with given reason that will
     * be visible in the game.
     * @param reason disconnect reason
     */
    public void disconnect(Component reason) {
        try {
            if(clientState == ClientState.LOGIN)
                sendPacket(new PacketLoginOutDisconnect(reason));
            if(clientState == ClientState.PLAY)
                sendPacket(new PacketPlayOutDisconnect(reason));
        } catch (Exception ignored) { }
        close();
    }

    /**
     * Client state of the connection, use to determinate the correct
     * group of packets to write/read.
     */
    @AllArgsConstructor
    public enum ClientState {
        HANDSHAKE(Packet.PacketState.HANDSHAKING_IN, Packet.PacketState.HANDSHAKING_OUT),
        STATUS(Packet.PacketState.STATUS_IN, Packet.PacketState.STATUS_OUT),
        LOGIN(Packet.PacketState.LOGIN_IN, Packet.PacketState.LOGIN_OUT),
        PLAY(Packet.PacketState.PLAY_IN, Packet.PacketState.PLAY_OUT),
        DISCONNECTED(null, null);

        protected final Packet.PacketState in;
        protected final Packet.PacketState out;

        public static ClientState fromState(Packet.PacketState state) {
            for(ClientState clientState : values()) {
                if(clientState.in == state || clientState.out == state)
                    return clientState;
            }
            return null;
        }
    }

}
