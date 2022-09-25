package me.pesekjak.machine.network.packets;

import lombok.Getter;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ZLib;

import java.io.IOException;

public abstract class Packet implements Cloneable {

    /**
     * @return mapped ID of the packet
     */
    public abstract int getID();

    /**
     * Serializes the packet data, doesn't contain packet size and ID.
     * @return serialized packet data
     */
    public abstract byte[] serialize();

    public abstract Packet clone();

    /**
     * Serializes the full packet.
     * @return serialized packet
     */
    public byte[] rawSerialize() {
        return new FriendlyByteBuf()
                .writeVarInt(getSize())
                .writeVarInt(getID())
                .writeBytes(serialize())
                .bytes();
    }

    /**
     * Returns the size of the packet ID and packet data.
     * @return size of the packet
     */
    public int getSize() {
        return new FriendlyByteBuf()
                .writeVarInt(getID())
                .writeBytes(serialize())
                .bytes().length;
    }

    /**
     * Serializes the full compress packet.
     * @param threshold threshold
     * @return serialized compressed packet
     */
    public byte[] rawCompressedSerialize(int threshold) {
        int size = getSize();
        if(size < threshold) { // Packet is too small to be compressed
            byte[] data = new FriendlyByteBuf().writeVarInt(0) // Empty Data length
                    .writeVarInt(getID())
                    .writeBytes(serialize())
                    .bytes();
            return new FriendlyByteBuf()
                    .writeVarInt(data.length)
                    .writeBytes(data)
                    .bytes();
        }
        byte[] dataLength = new FriendlyByteBuf()
                .writeVarInt(size)
                .bytes();
        byte[] compressed = getCompressedPacketData();
        return new FriendlyByteBuf()
                .writeVarInt(dataLength.length + compressed.length)
                .writeVarInt(size)
                .writeBytes(compressed)
                .bytes();
    }

    /**
     * Returns the compressed size of packet ID and packet data.
     * @return compressed size of the packet
     */
    public int getCompressedSize() {
        return getCompressedPacketData().length;
    }

    private byte[] getCompressedPacketData() {
        try {
            return ZLib.compress(new FriendlyByteBuf()
                    .writeVarInt(getID())
                    .writeBytes(serialize())
                    .bytes());
        } catch (IOException exception) { return new byte[0]; }
    }

    public enum PacketState {

        HANDSHAKING_IN (0b000),
        HANDSHAKING_OUT(0b001),
        STATUS_IN      (0b010),
        STATUS_OUT     (0b011),
        LOGIN_IN       (0b100),
        LOGIN_OUT      (0b101),
        PLAY_IN        (0b110),
        PLAY_OUT       (0b111);

        public static final int OFFSET = 12;

        @Getter
        private final int mask;

        PacketState(int mask) {
            this.mask = mask << OFFSET;
        }

        public static PacketState fromMask(int mask) {
            for(PacketState state : values()) {
                if(state.mask == mask) return state;
            }
            return null;
        }

    }

}
