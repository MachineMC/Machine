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
package org.machinemc.network.protocol;

/**
 * Represents a packet.
 *
 * @param <T> type of packet listener used to handle this packet
 */
public interface Packet<T extends PacketListener> {

    /**
     * Calls the handling method of the provided listener to
     * handle the packet.
     *
     * @param listener listener
     */
    void handle(T listener);

    /**
     * Returns packet flow of this packet.
     *
     * @return flow
     */
    PacketFlow flow();

}
