package me.pesekjak.machine.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.events.translations.TranslatorHandler;
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

public class ClientConnection extends Thread implements ServerProperty, AutoCloseable {

    private static final NamespacedKey DEFAULT_HANDLER_NAMESPACE = NamespacedKey.minecraft("default");

    @Getter
    private final Machine server;
    @Getter
    private final Socket clientSocket;
    @Getter
    protected Channel channel;
    @Getter @Setter
    private ClientState clientState;
    @Getter
    private long lastPacketTimestamp;

    @Getter @Setter
    private String loginUsername;

    @Getter @Nullable
    private Player owner;

    public ClientConnection(Machine server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.channel = null;
        this.lastPacketTimestamp = -1;
    }

    /**
     * Sends packet to the client.
     * @param packet packet sent to the client
     */
    public synchronized void sendPacket(PacketOut packet) throws IOException {
        if (channel.writePacket(packet))
            lastPacketTimestamp = System.currentTimeMillis();
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
            // Handshaking
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

            while (clientSocket.isConnected() && clientState != ClientState.DISCONNECTED)
                readPackets();

            close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        owner = null;
        channel.close();
        clientSocket.close();
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

    public void disconnect() {
        disconnect(Component.text("Disconnected"));
    }

    public void disconnect(Component reason) {
        if(clientState == ClientState.LOGIN) {
            try {
                PacketLoginOutDisconnect packet = new PacketLoginOutDisconnect(reason);
                sendPacket(packet);
            } catch (Exception ignored) { }
        }
        try {
            clientState = ClientState.DISCONNECTED;
            close();
        } catch (Exception ignored) { }
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
    }

}
