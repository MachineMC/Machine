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
import me.pesekjak.machine.utils.NamespacedKey;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

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
    private long keepAliveKey;
    private long lastSendKeepAlive;
    private long lastReadKeepAlive;

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
            setChannel(
                    new DataInputStream(clientSocket.getInputStream()),
                    new DataOutputStream(clientSocket.getOutputStream())
            );
            getChannel().addHandlerAfter(
                    NamespacedKey.machine("main"),
                    new TranslatorHandler(server.getTranslatorDispatcher())
            );

            AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();
            future.set(server.getConnection().executor.scheduleAtFixedRate(() -> {
                if(clientState == ClientState.DISCONNECTED)
                    future.get().cancel(true);
                try {
                    PacketIn[] packets = readPackets();
                    if(packets != null && packets.length != 0)
                        lastReadTimestamp = System.currentTimeMillis();
                } catch (Exception exception) {
                    ExceptionHandler.handle(new ClientException(this, exception));
                    clientState = ClientState.DISCONNECTED;
                }
                if(System.currentTimeMillis() - lastReadTimestamp > ServerConnection.READ_IDLE_TIMEOUT)
                    disconnect(Component.text("Timed out"));
            }, 0, 1000 / ServerConnection.TPS, TimeUnit.MILLISECONDS));

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
        keepAliveKey = new Random().nextLong();
        AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();
        future.set(server.getConnection().executor.scheduleAtFixedRate(() -> {
            int latency = calculateLatency();
            if(latency < 0)
                disconnect(Component.text("Timed out"));
            if(clientState != ClientState.PLAY) {
                future.get().cancel(true);
                return;
            }
            if(owner != null) owner.setLatency(latency);
            try {
                sendPacket(new PacketPlayOutKeepAlive(keepAliveKey));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }, 0, ServerConnection.KEEP_ALIVE_FREQ, TimeUnit.MILLISECONDS));
    }

    public void sendKeepAlive() {
        if(clientState != ClientState.PLAY)
            throw new IllegalStateException("Client isn't in the playing state");
        lastSendKeepAlive = System.currentTimeMillis();
    }

    public void readKeepAlive() {
        if(clientState != ClientState.PLAY)
            throw new IllegalStateException("Client isn't in the playing state");
        lastReadKeepAlive = System.currentTimeMillis();
    }

    private int calculateLatency() {
        return (int) (lastReadKeepAlive - lastSendKeepAlive);
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
