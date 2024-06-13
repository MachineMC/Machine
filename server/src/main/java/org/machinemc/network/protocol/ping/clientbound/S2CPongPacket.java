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
package org.machinemc.network.protocol.ping.clientbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.PacketIDMap;
import org.machinemc.network.protocol.PacketListener;
import org.machinemc.network.protocol.ping.PingPackets;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;

/**
 * Pong packet used to answer {@link org.machinemc.network.protocol.ping.serverbound.C2SPingPacket}.
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Status.ClientBound.NAME,
                PacketGroups.Play.ClientBound.NAME
        },
        catalogue = PingPackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CPongPacket implements org.machinemc.network.protocol.Packet<PacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Status.ClientBound.NAME, PacketGroups.Status.ClientBound.PONG,
                PacketGroups.Play.ClientBound.NAME, PacketGroups.Play.ClientBound.PONG
        );
    }

    /**
     * Packet ID used for verification. Should be the same as sent by the client.
     */
    private long payload;

    @Override
    public void handle(final PacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
