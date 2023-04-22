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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.network.packets;

import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;

/**
 * Creates packet instance from given data.
 * @param <T> packet
 */
@FunctionalInterface
public interface PacketCreator<T extends Packet> {

    /**
     * Creates new packet instance from given {@link org.machinemc.server.utils.FriendlyByteBuf},
     * the buffer can't contain the packet size and ID, but only data
     * itself.
     * @param buf buffer with packet data
     * @return new packet instance
     */
    T create(ServerBuffer buf);

}
