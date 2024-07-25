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
package org.machinemc.network.protocol.clientinformation.serverbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.entities.player.PlayerSettings;
import org.machinemc.network.protocol.clientinformation.ClientInformationPacketListener;
import org.machinemc.network.protocol.clientinformation.ClientInformationPackets;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.PacketIDMap;
import org.machinemc.paklet.Packet;
import org.machinemc.paklet.PacketID;

/**
 * Packet sent when the player connects, or when the settings are changed.
 */
@Data
@Packet(
        id = Packet.DYNAMIC_PACKET,
        group = {
                PacketGroups.Configuration.ServerBound.NAME,
                PacketGroups.Play.ServerBound.NAME,
        },
        catalogue = ClientInformationPackets.class
)
@NoArgsConstructor
@AllArgsConstructor
public class C2SClientInformationPacket implements org.machinemc.network.protocol.Packet<ClientInformationPacketListener> {

    @PacketID
    private static int id() {
        return PacketIDMap.compute(
                PacketGroups.Configuration.ServerBound.NAME, PacketGroups.Configuration.ServerBound.CLIENT_INFORMATION,
                PacketGroups.Play.ServerBound.NAME, PacketGroups.Play.ServerBound.CLIENT_INFORMATION
        );
    }

    private PlayerSettings settings;

    @Override
    public void handle(ClientInformationPacketListener listener) {
        listener.onClientInformation(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

}
