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
 * Represents a packet listener.
 * <p>
 * This class itself does not offer any packet listening methods and is expected to
 * be extended with methods for all listening packets.
 */
public interface PacketListener {

    /**
     * Returns protocol that is used by this listener in form of connection state.
     *
     * @return connection state
     */
    ConnectionState protocol();

}
