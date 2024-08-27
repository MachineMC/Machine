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
import org.machinemc.scriptive.components.Component;

/**
 * Packet sent by the server for client to exit the game.
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
public class S2CDisconnectPacket implements org.machinemc.network.protocol.Packet<LifeCyclePacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Configuration.ClientBound.NAME, PacketGroups.Configuration.ClientBound.DISCONNECT,
                PacketGroups.Play.ClientBound.NAME, PacketGroups.Play.ClientBound.DISCONNECT
        );
    }

    /**
     * The reason why the player was disconnected.
     */
    private Component reason;

    @Override
    public void handle(final LifeCyclePacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
