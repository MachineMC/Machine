package me.pesekjak.machine.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.events.translations.TranslatorHandler;
import me.pesekjak.machine.network.packets.Packet;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.in.PacketHandshakingInHandshake;
import me.pesekjak.machine.network.packets.in.PacketLoginInStart;
import me.pesekjak.machine.network.packets.in.PacketStatusInRequest;
import me.pesekjak.machine.network.packets.in.PacketStatusInPing;
import me.pesekjak.machine.network.packets.out.*;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
    private long lastPacketTimestamp;

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
    public synchronized PacketIn readPacket() throws IOException {
        assert channel != null;
        return channel.readPacket(clientState);
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

            PacketIn packet = readPacket();
            if(!(packet instanceof PacketHandshakingInHandshake handshake)) {
                close();
                return;
            }

            // Later defined in login and used in play
            String username = null;
            UUID uuid = null;

            switch (handshake.getHandshakeType()) {
                // Status
                case STATUS -> {
                    clientState = ClientState.STATUS;
                    while (clientSocket.isConnected()) {
                        PacketIn packetIn = readPacket();
                        if(packetIn instanceof PacketStatusInRequest) {
                            FriendlyByteBuf buf = new FriendlyByteBuf()
                                    .writeString(server.statusJson(), StandardCharsets.UTF_8);
                            sendPacket(new PacketStatusOutResponse(buf));
                        } else if(packetIn instanceof PacketStatusInPing packetPing) {
                            FriendlyByteBuf buf = new FriendlyByteBuf()
                                    .writeLong(packetPing.getPayload());
                            sendPacket(new PacketStatusOutPong(buf));
                            break;
                        }
                    }
                }
                // Login
                case LOGIN -> {
                    clientState = ClientState.LOGIN;
                    while (clientSocket.isConnected()) {
                        PacketIn packetIn = readPacket();
                        if (packetIn instanceof PacketLoginInStart packetLogin) {
                            username = packetLogin.getUsername();
                            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
                            FriendlyByteBuf buf = new FriendlyByteBuf()
                                    .writeUUID(uuid)
                                    .writeString(username, StandardCharsets.UTF_8)
                                    .writeVarInt(0);
                            sendPacket(new PacketLoginOutSuccess(buf));
                            clientState = ClientState.PLAY;
                            break;
                        }
                    }
                }
            }
            if(clientState == ClientState.PLAY) {
                if(username == null)
                    throw new IllegalStateException("Client with no username tried to login play");
                owner = new Player(server, uuid, username, this);
                while (clientSocket.isConnected()) {

                }
            }

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

    public void disconnect(Component reason) {
        if(clientState == ClientState.LOGIN) {
            try {
                FriendlyByteBuf buf = new FriendlyByteBuf()
                        .writeString(GsonComponentSerializer.gson().serialize(reason), StandardCharsets.UTF_8);
                PacketLoginOutDisconnect packet = new PacketLoginOutDisconnect(buf);
                sendPacket(packet);
                close();
            } catch (Exception ignored) { }
        }
    }

    @AllArgsConstructor
    public enum ClientState {
        HANDSHAKE(Packet.PacketState.HANDSHAKING_IN, Packet.PacketState.HANDSHAKING_OUT),
        STATUS(Packet.PacketState.STATUS_IN, Packet.PacketState.STATUS_OUT),
        LOGIN(Packet.PacketState.LOGIN_IN, Packet.PacketState.LOGIN_OUT),
        PLAY(Packet.PacketState.PLAY_IN, Packet.PacketState.PLAY_OUT);

        protected final Packet.PacketState in;
        protected final Packet.PacketState out;
    }

}
