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
import lombok.SneakyThrows;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.network.ClientConnection;
import org.machinemc.network.protocol.ConnectionState;
import org.machinemc.network.protocol.login.LoginPacketListener;
import org.machinemc.network.protocol.login.clientbound.S2CLoginSuccessPacket;
import org.machinemc.network.protocol.login.serverbound.C2SHelloPacket;
import org.machinemc.network.protocol.login.serverbound.C2SLoginAcknowledgedPacket;
import org.machinemc.network.protocol.pluginmessage.serverbound.C2SPluginMessagePacket;

/**
 * Login packet listener used by the server.
 */
public class ServerLoginPacketListener implements LoginPacketListener {

    private final ClientConnection connection;

    public ServerLoginPacketListener(final ClientConnection connection) {
        this.connection = Preconditions.checkNotNull(connection, "Client connection can not be null");
    }

    @Override
    @SneakyThrows
    public void onHello(final C2SHelloPacket packet) {
        // TODO encryption

        final int compressionThreshold = connection.getServer().getServerProperties().getCompressionThreshold();
        // skips the packet if the compression is disabled
        if (compressionThreshold >= 0) connection.setCompression(compressionThreshold).get();

        final GameProfile profile = GameProfile.forOfflinePlayer(packet.getUsername());
        connection.sendPacket(new S2CLoginSuccessPacket(profile, true), true);
    }

    @Override
    public void onLoginAcknowledged(final C2SLoginAcknowledgedPacket packet) {
        connection.setupInboundProtocol(ConnectionState.CONFIGURATION, new ServerConfigurationPacketListener(connection));
        connection.setupOutboundProtocol(ConnectionState.CONFIGURATION);
    }

    @Override
    public void onPluginMessage(final C2SPluginMessagePacket packet) {
        // TODO
    }

}
