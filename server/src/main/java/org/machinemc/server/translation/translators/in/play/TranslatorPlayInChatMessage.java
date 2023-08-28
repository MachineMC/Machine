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

import org.machinemc.api.chat.*;
import org.machinemc.api.entities.Player;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.server.chat.*;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.in.play.PacketPlayInChatMessage;
import org.machinemc.server.translation.PacketTranslator;

import java.util.Optional;

public class TranslatorPlayInChatMessage extends PacketTranslator<PacketPlayInChatMessage> {

    @Override
    public boolean translate(final ClientConnection connection, final PacketPlayInChatMessage packet) {
        if (connection.getOwner().isEmpty())
            return false;
        final ServerPlayer player = connection.getOwner().get();
        if (!Messenger.canReceiveMessage(player)) {
            connection.getServer().getMessenger().sendRejectionMessage(player);
            return false;
        }
        return true;
    }

    @Override
    public void translateAfter(final ClientConnection connection, final PacketPlayInChatMessage packet) {
        if (connection.getOwner().isEmpty())
            return;
        final ServerPlayer player = connection.getOwner().get();

        final ChatType chatType = player.getServer().getMessenger()
                .getChatType(NamespacedKey.minecraft("chat"))
                .orElseThrow(() -> new NullPointerException("Missing chat type 'minecraft:chat'"));

        final PlayerMessage message;
        final ChatBound chatBound = new ServerChatBound(connection.getServer().getMessenger(), chatType, player.getDisplayName(), null);
        final LastSeenMessages.Update update = new LastSeenMessages.Update(packet.getMessageCount(), packet.getAcknowledged());
        final Optional<LastSeenMessages> lastMessages = player.getMessageChain().applyUpdate(update);
        if (lastMessages.isEmpty()) {
            connection.getServerConsole().warning("Failed to validate message acknowledgements from '" + player.getName() + "'");
            connection.disconnect(TranslationComponent.of("multiplayer.disconnect.chat_validation_failed"));
            return;
        }

        if (player.getChatSession().isPresent()) {
            message = new PlayerChatMessage(
                    new SignedMessageHeader(player.getUUID(), player.getNextMessageID(), packet.getMessageSignature()),
                    new SignedMessageBody(packet.getMessage(), packet.getTimestamp(), packet.getSalt()),
                    lastMessages.get().pack().entries(),
                    null,
                    FilterType.PASS_THROUGH,
                    null,
                    chatBound
            );
        } else {
            message = PlayerChatMessage.unsigned(player.getUUID(), packet.getMessage(), chatBound);
        }

        for (final Player serverPlayer : connection.getServer().getPlayers())
            serverPlayer.sendMessage(message);
    }

    @Override
    public Class<PacketPlayInChatMessage> packetClass() {
        return PacketPlayInChatMessage.class;
    }

}
