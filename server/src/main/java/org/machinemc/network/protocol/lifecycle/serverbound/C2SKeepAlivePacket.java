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
package org.machinemc.network.protocol.lifecycle.serverbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.PacketIDMap;
import org.machinemc.network.protocol.lifecycle.LifeCyclePacketListener;
import org.machinemc.network.protocol.lifecycle.LifeCyclePackets;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;

/**
 * The server will frequently send out a keep-alive,
 * each containing a random ID. The client must respond with the same packet.
 *
 * @see org.machinemc.network.protocol.lifecycle.clientbound.S2CKeepAlivePacket
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Configuration.ServerBound.NAME,
                PacketGroups.Play.ServerBound.NAME
        },
        catalogue = LifeCyclePackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class C2SKeepAlivePacket implements org.machinemc.network.protocol.Packet<LifeCyclePacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Configuration.ServerBound.NAME, PacketGroups.Configuration.ServerBound.KEEP_ALIVE,
                PacketGroups.Play.ServerBound.NAME, PacketGroups.Play.ServerBound.KEEP_ALIVE
        );
    }

    /**
     * Packet ID used for verification. Should be the same as sent by the client.
     */
    private long payload;

    @Override
    public void handle(final LifeCyclePacketListener listener) {
        listener.onKeepAlive(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
