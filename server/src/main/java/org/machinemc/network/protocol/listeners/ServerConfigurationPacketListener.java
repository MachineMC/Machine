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

import org.machinemc.network.ClientConnection;
import org.machinemc.network.protocol.clientinformation.serverbound.C2SClientInformationPacket;
import org.machinemc.network.protocol.configuration.ConfigurationPacketListener;
import org.machinemc.network.protocol.configuration.serverbound.C2SAcknowledgeFinishConfigurationPacket;
import org.machinemc.network.protocol.pluginmessage.serverbound.C2SPluginMessagePacket;

/**
 * Configuration packet listener used by the server.
 */
public class ServerConfigurationPacketListener implements ConfigurationPacketListener {

    private final ClientConnection connection;

    public ServerConfigurationPacketListener(final ClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public void onAcknowledgeFinishConfiguration(final C2SAcknowledgeFinishConfigurationPacket packet) {
        // TODO switch to play
        throw new UnsupportedOperationException();
    }

    @Override
    public void onClientInformation(final C2SClientInformationPacket packet) {
        connection.setPlayerSettings(packet.getSettings());
    }

    @Override
    public void onPluginMessage(final C2SPluginMessagePacket packet) {
        // TODO
    }

}
