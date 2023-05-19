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

import org.machinemc.api.chat.MessageType;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.entities.Player;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.util.ChatUtils;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.play.PacketPlayInChatMessage;
import org.machinemc.server.translation.PacketTranslator;

public class TranslatorPlayInChatMessage extends PacketTranslator<PacketPlayInChatMessage> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketPlayInChatMessage packet) {
        final ServerPlayer player = connection.getOwner();
        if (player == null)
            return false;
        if (!Messenger.canReceiveMessage(player)) {
            connection.getServer().getMessenger().sendRejectionMessage(player);
            return false;
        }
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketPlayInChatMessage packet) {
        final ServerPlayer player = connection.getOwner();
        if (player == null)
            return;
        final String message = ChatUtils.DEFAULT_CHAT_FORMAT
                .replace("%name%", player.getName())
                .replace("%message%", packet.getMessage());
        for (final Player serverPlayer : connection.getServer().getPlayerManager().getPlayers())
            serverPlayer.sendMessage(player.getUuid(), TextComponent.of(message), MessageType.SYSTEM);
        connection.getServer().getConsole().info(message);
    }

    @Override
    public Class<PacketPlayInChatMessage> packetClass() {
        return PacketPlayInChatMessage.class;
    }

}
