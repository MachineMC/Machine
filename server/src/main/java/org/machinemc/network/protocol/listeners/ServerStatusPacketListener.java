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

import lombok.RequiredArgsConstructor;
import org.machinemc.network.ClientConnection;
import org.machinemc.network.protocol.ping.clientbound.S2CPongPacket;
import org.machinemc.network.protocol.ping.serverbound.C2SPingPacket;
import org.machinemc.network.protocol.status.StatusPacketListener;
import org.machinemc.network.protocol.status.clientbound.S2CStatusResponsePacket;
import org.machinemc.network.protocol.status.clientbound.ServerStatus;
import org.machinemc.network.protocol.status.serverbound.C2SStatusRequestPacket;
import org.machinemc.scriptive.components.TextComponent;

/**
 * Status packet listener used by the server.
 */
@RequiredArgsConstructor
public class ServerStatusPacketListener implements StatusPacketListener {

    private final ClientConnection connection;

    @Override
    public void onStatusRequest(final C2SStatusRequestPacket packet) {
        final ServerStatus status = new ServerStatus(
                new ServerStatus.Version("1.21", 767),
                null,
                TextComponent.of("A Machine Server"),
                false);
        connection.sendPacket(new S2CStatusResponsePacket(status), true);
    }

    @Override
    public void onPing(final C2SPingPacket packet) {
        connection.sendPacket(new S2CPongPacket(packet.getPayload()), true);
    }

}
