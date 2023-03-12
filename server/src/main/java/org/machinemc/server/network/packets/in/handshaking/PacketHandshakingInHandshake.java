package org.machinemc.server.network.packets.in.handshaking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.Range;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketHandshakingInHandshake extends PacketIn {

    private static final int ID = 0x00;

    private int protocolVersion;
    private String serverAddress;
    private int serverPort;
    private HandshakeType handshakeType;

    static {
        register(PacketHandshakingInHandshake.class, ID, PacketState.HANDSHAKING_IN,
                PacketHandshakingInHandshake::new
        );
    }

    public PacketHandshakingInHandshake(ServerBuffer buf) {
        protocolVersion = buf.readVarInt();
        serverAddress = buf.readString(StandardCharsets.UTF_8);
        serverPort = buf.readShort() & 0xFFFF;
        handshakeType = HandshakeType.fromID(buf.readVarInt());
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.HANDSHAKING_IN;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(protocolVersion)
                .writeString(serverAddress, StandardCharsets.UTF_8)
                .writeShort((short) (serverPort & 0xFFFF))
                .writeVarInt(handshakeType.ID)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketHandshakingInHandshake(new FriendlyByteBuf(serialize()));
    }

    @AllArgsConstructor
    public enum HandshakeType {
        STATUS(1),
        LOGIN(2);

        @Getter
        private final int ID;

        public static HandshakeType fromID(@Range(from = 1, to = 2) int ID) {
            for (HandshakeType type : HandshakeType.values()) {
                if (type.getID() == ID) return type;
            }
            throw new RuntimeException("Unsupported Handshake type");
        }
    }

}