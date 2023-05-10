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
package org.machinemc.server.translation.translators.in.play;

import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.play.PacketPlayInConfirmTeleportation;
import org.machinemc.server.translation.PacketTranslator;

public class TranslatorPlayInConfirmTeleportation extends PacketTranslator<PacketPlayInConfirmTeleportation> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketPlayInConfirmTeleportation packet) {
        final ServerPlayer player = connection.getOwner();
        if (player == null)
            return false;
        if (!player.isTeleporting() || player.getTeleportId() != packet.getTeleportId()) {
            player.setTeleporting(false);
            // connection.disconnect(TranslationComponent.of("multiplayer.disconnect.invalid_player_movement"));
            System.out.println(player.isTeleporting() + " "
                    + player.getTeleportId() + "="
                    + packet.getTeleportId());
            return false;
        }
        player.setTeleporting(false);
        player.setLocation(player.getTeleportLocation());
        player.setTeleportLocation(null);
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketPlayInConfirmTeleportation packet) {

    }

    @Override
    public Class<PacketPlayInConfirmTeleportation> packetClass() {
        return PacketPlayInConfirmTeleportation.class;
    }

}
