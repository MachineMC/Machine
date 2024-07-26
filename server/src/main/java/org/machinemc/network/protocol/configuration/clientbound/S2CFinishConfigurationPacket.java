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
package org.machinemc.network.protocol.configuration.clientbound;

import lombok.Data;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.configuration.ConfigurationPacketListener;
import org.machinemc.paklet.Packet;

/**
 * Packet sent by the server for client to exit the configuration phase.
 */
@Data
@Packet(
        id = PacketGroups.Configuration.ClientBound.FINISH_CONFIGURATION,
        group = PacketGroups.Configuration.ClientBound.NAME,
        catalogue = PacketGroups.Configuration.ClientBound.class
)
public class S2CFinishConfigurationPacket implements org.machinemc.network.protocol.Packet<ConfigurationPacketListener> {

    @Override
    public void handle(final ConfigurationPacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
