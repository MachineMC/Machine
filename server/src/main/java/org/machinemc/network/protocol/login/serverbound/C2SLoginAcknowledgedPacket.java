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
package org.machinemc.network.protocol.login.serverbound;

import lombok.Data;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.paklet.Packet;

/**
 * Acknowledgement to the {@link org.machinemc.network.protocol.login.clientbound.S2CLoginSuccessPacket}
 * packet sent by the server.
 */
@Data
@Packet(
        id = PacketGroups.Login.ServerBound.LOGIN_ACKNOWLEDGED,
        group = PacketGroups.Login.ServerBound.NAME,
        catalogue = PacketGroups.Login.ServerBound.class
)
public class C2SLoginAcknowledgedPacket implements org.machinemc.network.protocol.Packet<LoginPacketListener> {

    @Override
    public void handle(final LoginPacketListener listener) {
        listener.onLoginAcknowledged(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

}
