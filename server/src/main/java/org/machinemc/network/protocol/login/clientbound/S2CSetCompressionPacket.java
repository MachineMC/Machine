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
package org.machinemc.network.protocol.login.clientbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.paklet.Packet;

/**
 * Packet sent by the server for client to change the compression settings.
 */
@Data
@Packet(
        id = PacketGroups.Login.ClientBound.SET_COMPRESSION,
        group = PacketGroups.Login.ClientBound.NAME,
        catalogue = PacketGroups.Login.ClientBound.class
)
@NoArgsConstructor
@AllArgsConstructor
public class S2CSetCompressionPacket implements org.machinemc.network.protocol.Packet<LoginPacketListener> {

    private int threshold;

    @Override
    public void handle(final LoginPacketListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

}
