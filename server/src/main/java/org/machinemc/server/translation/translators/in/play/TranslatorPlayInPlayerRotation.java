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

import org.machinemc.api.world.EntityPosition;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.play.PacketPlayInPlayerRotation;
import org.machinemc.server.translation.PacketTranslator;

public class TranslatorPlayInPlayerRotation extends PacketTranslator<PacketPlayInPlayerRotation> {

    private ServerPlayer player;
    private EntityPosition position;

    @Override
    public boolean translate(final ClientConnection connection, final PacketPlayInPlayerRotation packet) {
        player = connection.getOwner();
        if (player == null)
            return false;
        position = player.getLocation();
        position.setYaw(packet.getYaw());
        position.setPitch(packet.getPitch());
        if (EntityPosition.isInvalid(position)) {
            connection.disconnect(TranslationComponent.of("multiplayer.disconnect.invalid_player_movement"));
            return false;
        }
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketPlayInPlayerRotation packet) {
        player.handleMovement(position, packet.isOnGround());
    }

    @Override
    public Class<PacketPlayInPlayerRotation> packetClass() {
        return PacketPlayInPlayerRotation.class;
    }

}
