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
package org.machinemc.network.protocol.configuration.serverbound;

import lombok.Data;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.configuration.ConfigurationPacketListener;
import org.machinemc.paklet.Packet;

/**
 * Acknowledgement to the {@link org.machinemc.network.protocol.configuration.clientbound.S2CFinishConfigurationPacket}
 * packet sent by the server.
 */
@Data
@Packet(
        id = PacketGroups.Configuration.ServerBound.ACKNOWLEDGE_FINISH_CONFIGURATION,
        group = PacketGroups.Configuration.ServerBound.NAME,
        catalogue = PacketGroups.Configuration.ServerBound.class
)
public class C2SAcknowledgeFinishConfigurationPacket implements org.machinemc.network.protocol.Packet<ConfigurationPacketListener> {

    @Override
    public void handle(final ConfigurationPacketListener listener) {
        listener.onAcknowledgeFinishConfiguration(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

}
