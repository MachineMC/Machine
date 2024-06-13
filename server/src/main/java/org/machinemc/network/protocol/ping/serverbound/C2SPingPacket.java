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
package org.machinemc.network.protocol.ping.serverbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.PacketIDMap;
import org.machinemc.network.protocol.ping.PingPacketListener;
import org.machinemc.network.protocol.ping.PingPackets;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;

/**
 * Ping packet used during server status phase for calculation of the server ping.
 * <p>
 * The payload may be any number.
 * Notchian clients use a system-dependent time value which is counted in milliseconds.
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Status.ServerBound.NAME,
                PacketGroups.Play.ServerBound.NAME
        },
        catalogue = PingPackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class C2SPingPacket implements org.machinemc.network.protocol.Packet<PingPacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Status.ServerBound.NAME, PacketGroups.Status.ServerBound.PING,
                PacketGroups.Play.ServerBound.NAME, PacketGroups.Play.ServerBound.PING
        );
    }

    /**
     * Packet ID used for verification.
     */
    private long payload;

    @Override
    public void handle(final PingPacketListener listener) {
        listener.onPing(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

}
