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
package org.machinemc.network.protocol.clientinformation;

import org.machinemc.network.protocol.PacketListener;
import org.machinemc.network.protocol.clientinformation.serverbound.C2SClientInformationPacket;

/**
 * Packet listener for client information packets.
 * <p>
 * This packet listener is meant to be shared by multiple implementations.
 */
public interface ClientInformationPacketListener extends PacketListener {

    /**
     * Called when client information packet is received.
     *
     * @param packet packet
     */
    void onClientInformation(C2SClientInformationPacket packet);

}
