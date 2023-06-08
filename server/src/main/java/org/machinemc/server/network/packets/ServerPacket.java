/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.network.packets;

import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.server.utils.ZLib;

import java.io.IOException;

/**
 * Default packet implementation.
 */
public abstract class ServerPacket implements Packet {

    /**
     * @return mapped ID of the packet
     */
    public abstract int getID();

    /**
     * Serializes the packet data, doesn't contain packet size and ID.
     * @return serialized packet data
     */
    public abstract byte[] serialize();

    /**
     * @return clone of the packet
     */
    public abstract ServerPacket clone();

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
    public byte[] rawCompressedSerialize(final int threshold) {
        final int size = getSize();
        if (size < threshold) { // Packet is too small to be compressed
            final byte[] data = new FriendlyByteBuf().writeVarInt(0) // Empty Data length
                    .writeVarInt(getID())
                    .writeBytes(serialize())
                    .bytes();
            return new FriendlyByteBuf()
                    .writeVarInt(data.length)
                    .writeBytes(data)
                    .bytes();
        }
        final byte[] dataLength = new FriendlyByteBuf()
                .writeVarInt(size)
                .bytes();
        final byte[] compressed = getCompressedPacketData();
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
                    .writeVarInt(getID())
                    .writeBytes(serialize())
                    .bytes());
        } catch (IOException exception) {
            return new byte[0];
        }
    }

    @Override
    public String toString() {
        return "Packet";
    }

}
