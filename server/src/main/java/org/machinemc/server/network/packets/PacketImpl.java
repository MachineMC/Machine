package org.machinemc.server.network.packets;

import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.server.utils.ZLib;

import java.io.IOException;

/**
 * Default packet implementation.
 */
public abstract class PacketImpl implements Packet {

    /**
     * @return mapped ID of the packet
     */
    public abstract int getId();

    /**
     * Serializes the packet data, doesn't contain packet size and ID.
     * @return serialized packet data
     */
    public abstract byte[] serialize();

    /**
     * @return clone of the packet
     */
    public abstract PacketImpl clone();

    /**
     * Serializes the full packet.
     * @return serialized packet
     */
    public byte[] rawSerialize() {
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
    public byte[] rawCompressedSerialize(final int threshold) {
        int size = getSize();
        if (size < threshold) { // Packet is too small to be compressed
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

    /**
     * @return compressed packet data, id included
     */
    private byte[] getCompressedPacketData() {
        try {
            return ZLib.compress(new FriendlyByteBuf()
                    .writeVarInt(getId())
                    .writeBytes(serialize())
                    .bytes());
        } catch (IOException exception) {
            return new byte[0];
        }
    }

}
