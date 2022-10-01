package me.pesekjak.machine.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.auth.PublicKeyData;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.events.translations.TranslatorHandler;
import me.pesekjak.machine.exception.ClientException;
import me.pesekjak.machine.exception.ExceptionHandler;
import me.pesekjak.machine.network.packets.Packet;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.*;
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
    public synchronized boolean sendPacket(PacketOut packet) throws IOException {
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
     * Reads packet from the client.
     * @return packet sent by client
     */
    public synchronized PacketIn[] readPackets() throws IOException {
        assert channel != null;
        if(clientState == ClientState.DISCONNECTED)
            return null;
        return channel.readPackets();
    }

    private void setChannel(DataInputStream input, DataOutputStream output) {
        this.channel = new Channel(this, input, output);
        this.channel.addHandlerBefore(DEFAULT_HANDLER_NAMESPACE, new PacketHandler());
    }

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
                            ExceptionHandler.handle(new ClientException(this, exception));
                            clientState = ClientState.DISCONNECTED;
                            return null;
                        }
                        if(System.currentTimeMillis() - lastReadTimestamp > ServerConnection.READ_IDLE_TIMEOUT)
                            disconnect(Component.text("Timed out"));
                        return null;
                    }))
                    .async()
                    .repeat(true)
                    .period(1000 / server.getTPS())
                    .run(server.getScheduler());

        } catch (Exception ignored) { }
    }

    @Override
    public void close() throws Exception {
        clientState = ClientState.DISCONNECTED;
        if(owner != null && owner.isActive()) owner.remove();
        owner = null;
        channel.close();
        clientSocket.close();
        server.getConnection().disconnect(this);
    }

    public void setClientState(ClientState clientState) {
        if(clientState == ClientState.DISCONNECTED)
            throw new UnsupportedOperationException("You can't set the connection's state to disconnected");
        this.clientState = clientState;
    }

    public SecretKey getSecretKey() {
        return channel.getSecretKey();
    }

    public void setSecretKey(SecretKey key) {
        channel.setSecretKey(key);
    }

    public void setOwner(Player player) {
        if(owner != null)
            throw new IllegalStateException("Connection has been already initialized");
        if(!player.getName().equals(loginUsername))
            throw new IllegalStateException("Player's name and login name has to match");
        owner = player;
    }

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

    public void keepAlive() {
        if(clientState != ClientState.PLAY)
            throw new IllegalStateException("Client isn't in the playing state");
        if(keepAliveKey == -1)
            throw new IllegalStateException("Connection isn't being kept alive");
        if(owner != null) owner.setLatency((int) (System.currentTimeMillis() - lastKeepAlive));
    }

    public void disconnect() {
        disconnect(Component.text("Disconnected"));
    }

    public void disconnect(Component reason) {
        try {
            if(clientState == ClientState.LOGIN)
                sendPacket(new PacketLoginOutDisconnect(reason));
        } catch (Exception ignored) { }
        try {
            close();
        } catch (Exception exception) {
            ExceptionHandler.handle(new ClientException(this, exception));
        }
    }

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
