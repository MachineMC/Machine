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
package org.machinemc.server.translation.translators.in.handshaking;

import org.machinemc.api.network.PlayerConnection;
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.handshaking.PacketHandshakingInHandshake;

public class TranslatorHandshakingInHandshake extends PacketTranslator<PacketHandshakingInHandshake> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketHandshakingInHandshake packet) {
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketHandshakingInHandshake packet) {
        if (packet.getHandshakeType() == PacketHandshakingInHandshake.HandshakeType.STATUS) {
            connection.setState(PlayerConnection.ClientState.STATUS);
        } else if (packet.getHandshakeType() == PacketHandshakingInHandshake.HandshakeType.LOGIN) {
            connection.setState(PlayerConnection.ClientState.LOGIN);
        }
    }

    @Override
    public Class<PacketHandshakingInHandshake> packetClass() {
        return PacketHandshakingInHandshake.class;
    }

}
