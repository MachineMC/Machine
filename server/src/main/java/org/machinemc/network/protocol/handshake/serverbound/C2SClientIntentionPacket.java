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
package org.machinemc.network.protocol.handshake.serverbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.machinemc.network.protocol.PacketFlow;
import org.machinemc.network.protocol.PacketGroups;
import org.machinemc.network.protocol.handshake.HandshakePacketListener;
import org.machinemc.paklet.Packet;

/**
 * Handshaking packet for initializing the server connection.
 */
@Data
@Packet(
        id = PacketGroups.Handshaking.ServerBound.CLIENT_INTENTION,
        group = PacketGroups.Handshaking.ServerBound.NAME,
        catalogue = PacketGroups.Handshaking.ServerBound.class
)
@NoArgsConstructor
@AllArgsConstructor
public class C2SClientIntentionPacket implements org.machinemc.network.protocol.Packet<HandshakePacketListener> {

    /**
     * Protocol version number.
     */
    private int protocolVersion;

    /**
     * Server hostname.
     */
    private String hostName;

    /**
     * Server port.
     */
    private short port;

    /**
     * Specifies next client action.
     */
    private ClientIntent intent;

    @Override
    public void handle(final HandshakePacketListener listener) {
        listener.onClientIntention(this);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

}
