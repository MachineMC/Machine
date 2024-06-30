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
package org.machinemc.network.protocol.listeners;

import com.google.common.base.Preconditions;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.network.ClientConnection;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.network.protocol.login.clientbound.S2CLoginSuccessPacket;
import org.machinemc.network.protocol.login.serverbound.C2SHelloPacket;
import org.machinemc.network.protocol.login.serverbound.C2SLoginAcknowledgedPacket;

/**
 * Login packet listener used by the server.
 */
public class ServerLoginPacketListener implements LoginPacketListener {

    private final ClientConnection connection;

    public ServerLoginPacketListener(final ClientConnection connection) {
        this.connection = Preconditions.checkNotNull(connection, "Client connection can not be null");
    }

    @Override
    public void onHello(final C2SHelloPacket packet) {
        // TODO encryption
        continueLogin(GameProfile.forOfflinePlayer(packet.getUsername()));
    }

    private void continueLogin(final GameProfile profile) {
        // TODO set compression
        connection.sendPacket(new S2CLoginSuccessPacket(profile, true), true);
    }

    @Override
    public void onLoginAcknowledged(final C2SLoginAcknowledgedPacket packet) {
        // TODO switch to configuration
        throw new UnsupportedOperationException();
    }

}
