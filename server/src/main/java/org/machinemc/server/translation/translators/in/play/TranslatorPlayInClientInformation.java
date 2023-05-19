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
import org.machinemc.server.translation.PacketTranslator;
import org.machinemc.server.network.packets.in.play.PacketPlayInClientInformation;

public class TranslatorPlayInClientInformation extends PacketTranslator<PacketPlayInClientInformation> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketPlayInClientInformation packet) {
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketPlayInClientInformation packet) {
        final ServerPlayer player = connection.getOwner();
        if (player == null)
            return;
        player.setLocale(packet.getLocale());
        player.setViewDistance(packet.getViewDistance());
        player.setChatMode(packet.getChatMode());
        player.setDisplayedSkinParts(packet.getDisplayedSkinParts());
        player.setMainHand(packet.getMainHand());
    }

    @Override
    public Class<PacketPlayInClientInformation> packetClass() {
        return PacketPlayInClientInformation.class;
    }

}
