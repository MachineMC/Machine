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
package org.machinemc.network.protocol.lifecycle.clientbound;

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
 * The server will frequently send out a keep-alive, each containing a random ID.
 * The client must respond with the same payload.
 * <p>
 * If the client does not respond to a Keep Alive packet within 15 seconds after it was sent,
 * the server kicks the client. Vice versa, if the server does not send any keep-alive for 20 seconds,
 * the client will disconnect and yields a "Timed out" exception.
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Configuration.ClientBound.NAME,
                PacketGroups.Play.ClientBound.NAME
        },
        catalogue = LifeCyclePackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CKeepAlivePacket implements org.machinemc.network.protocol.Packet<LifeCyclePacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Configuration.ClientBound.NAME, PacketGroups.Configuration.ClientBound.KEEP_ALIVE,
                PacketGroups.Play.ClientBound.NAME, PacketGroups.Play.ClientBound.KEEP_ALIVE
        );
    }

    /**
     * Packet ID used for verification. Should be the same as sent by the client.
     */
    private long payload;

    @Override
    public void handle(final LifeCyclePacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
