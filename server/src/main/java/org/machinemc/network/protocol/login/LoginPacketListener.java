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
package org.machinemc.network.protocol.login;

import org.machinemc.network.protocol.ConnectionState;
import org.machinemc.network.protocol.PacketListener;
import org.machinemc.network.protocol.login.serverbound.C2SHelloPacket;
import org.machinemc.network.protocol.login.serverbound.C2SLoginAcknowledgedPacket;

/**
 * Packet listener for login packets.
 */
public interface LoginPacketListener extends PacketListener {

    /**
     * Called when client starts logging in process.
     *
     * @param packet packet
     */
    void onHello(C2SHelloPacket packet);

    /**
     * Called as a response to {@link org.machinemc.network.protocol.login.clientbound.S2CLoginSuccessPacket}
     * packet.
     *
     * @param packet packet
     */
    void onLoginAcknowledged(C2SLoginAcknowledgedPacket packet);

    @Override
    default ConnectionState protocol() {
        return ConnectionState.LOGIN;
    }

}
