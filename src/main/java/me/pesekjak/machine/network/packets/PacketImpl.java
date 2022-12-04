package me.pesekjak.machine.network.packets;

import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ZLib;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class PacketImpl implements Packet {

    /**
     * @return mapped ID of the packet
     */
    public abstract int getId();

    /**
     * Serializes the packet data, doesn't contain packet size and ID.
     * @return serialized packet data
     */
    public abstract byte @NotNull [] serialize();

    public abstract @NotNull PacketImpl clone();

    /**
     * Serializes the full packet.
     * @return serialized packet
     */
    public byte @NotNull [] rawSerialize() {
        return new FriendlyByteBuf()
                .writeVarInt(getSize())
                .writeVarInt(getId())
                .writeBytes(serialize())
                .bytes();
    }

    /**
     * Returns the size of the packet ID and packet data.
     * @return size of the packet
     */
    public int getSize() {
        return new FriendlyByteBuf()
                .writeVarInt(getId())
                .writeBytes(serialize())
                .bytes().length;
    }

    /**
     * Serializes the full compress packet.
     * @param threshold threshold
     * @return serialized compressed packet
     */
    public byte @NotNull [] rawCompressedSerialize(int threshold) {
        int size = getSize();
        if(size < threshold) { // Packet is too small to be compressed
            byte[] data = new FriendlyByteBuf().writeVarInt(0) // Empty Data length
                    .writeVarInt(getId())
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

    private byte @NotNull [] getCompressedPacketData() {
        try {
            return ZLib.compress(new FriendlyByteBuf()
                    .writeVarInt(getId())
                    .writeBytes(serialize())
                    .bytes());
        } catch (IOException exception) { return new byte[0]; }
    }

}
