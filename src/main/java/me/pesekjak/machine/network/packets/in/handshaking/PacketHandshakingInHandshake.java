package me.pesekjak.machine.network.packets.in.handshaking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public class PacketHandshakingInHandshake extends PacketIn {

    private static final int ID = 0x00;

    @Getter @Setter
    private int protocolVersion;
    @Getter @Setter @NotNull
    private String serverAddress;
    @Getter @Setter
    private int serverPort;
    @Getter @Setter @NotNull
    private HandshakeType handshakeType;

    static {
        register(PacketHandshakingInHandshake.class, ID, PacketState.HANDSHAKING_IN,
                PacketHandshakingInHandshake::new
        );
    }

    public PacketHandshakingInHandshake(FriendlyByteBuf buf) {
        protocolVersion = buf.readVarInt();
        serverAddress = buf.readString();
        serverPort = buf.readShort() & 0xFFFF;
        handshakeType = HandshakeType.fromID(buf.readVarInt());
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(protocolVersion)
                .writeString(serverAddress)
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

        public static @NotNull HandshakeType fromID(@Range(from = 1, to = 2) int ID) {
            for (HandshakeType type : HandshakeType.values()) {
                if (type.getID() == ID) return type;
            }
            throw new RuntimeException("Unsupported Handshake type");
        }
    }

}