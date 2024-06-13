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
package org.machinemc.network.protocol.status.clientbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.PacketListener;
import org.machinemc.paklet.Packet;

@Data
@Packet(
        id = PacketGroups.Status.ClientBound.STATUS_RESPONSE,
        group = PacketGroups.Status.ClientBound.NAME,
        catalogue = PacketGroups.Status.ClientBound.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CStatusResponsePacket implements org.machinemc.network.protocol.Packet<PacketListener> {

    private ServerStatus status;

    @Override
    public void handle(final PacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
