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
import org.machinemc.Machine;
import org.machinemc.network.ClientConnection;
import org.machinemc.network.protocol.ConnectionState;
import org.machinemc.network.protocol.handshake.HandshakePacketListener;
import org.machinemc.network.protocol.handshake.serverbound.C2SClientIntentionPacket;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.TranslationComponent;

/**
 * Handshake packet listener used by the server.
 */
public class ServerHandshakePacketListener implements HandshakePacketListener {

    private final ClientConnection connection;

    public ServerHandshakePacketListener(final ClientConnection connection) {
        this.connection = Preconditions.checkNotNull(connection, "Client connection can not be null");
    }

    @Override
    public void onClientIntention(final C2SClientIntentionPacket packet) {
        connection.setProtocolVersion(packet.getProtocolVersion());
        switch (packet.getIntent()) {
            case UNUSED -> throw new UnsupportedOperationException();
            case STATUS -> switchToStatus();
            case LOGIN -> switchToLogin(false);
            case TRANSFER -> switchToLogin(true);
        }
    }

    /**
     * Switches the client connection to the status state.
     */
    private void switchToStatus() {
        connection.setupInboundProtocol(ConnectionState.STATUS, new ServerStatusPacketListener(connection));
        connection.setupOutboundProtocol(ConnectionState.STATUS);
    }

    /**
     * Switches the client connection to the login state.
     *
     * @param transfer whether the client was transferred from another server
     */
    private void switchToLogin(final boolean transfer) {
        connection.setupInboundProtocol(ConnectionState.LOGIN, new ServerLoginPacketListener(connection));
        connection.setupOutboundProtocol(ConnectionState.LOGIN);

        final Machine server = connection.getServer();
        if (connection.getProtocolVersion() == server.getProtocolVersion()) return;

        // above 1.11.2
        if (connection.getProtocolVersion() > 316) {
            // TODO event
            connection.disconnect(TranslationComponent.of("multiplayer.disconnect.outdated_client", TextComponent.of(server.getMinecraftVersion())));
            return;
        }

        // There is no translation for outdated client in older versions
        // TODO event
        connection.disconnect(TextComponent.of("Outdated client! Please use " + server.getMinecraftVersion()));
    }
}
