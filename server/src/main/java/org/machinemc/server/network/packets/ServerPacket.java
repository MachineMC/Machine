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
        final byte[] id = new FriendlyByteBuf()
                .writeVarInt(getID())
                .bytes();
        final byte[] data = serialize();
        return new FriendlyByteBuf()
                .writeVarInt(id.length + data.length)
                .writeBytes(id)
                .writeBytes(data)
                .bytes();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
