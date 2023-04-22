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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.chat;

import org.machinemc.api.entities.Player;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.scriptive.components.Component;

public interface Messenger extends CodecPart, ServerProperty {

    /**
     * Checks if player can receive a message.
     * @param player player to check
     * @return if player can receive a message
     */
    static boolean canReceiveMessage(Player player) {
        return player.getChatMode() == ChatMode.ENABLED;
    }

    /**
     * Checks if player can receive a command.
     * @param player player to check
     * @return if player can receive a command
     */
    static boolean canReceiveCommand(Player player) {
        return player.getChatMode() != ChatMode.HIDDEN;
    }

    /**
     * Checks if player accepts given message type.
     * @param player player to check
     * @param messageType message type to check
     * @return if player accepts given message type
     */
    static boolean accepts(Player player, MessageType messageType) {
        if (messageType == MessageType.CHAT && canReceiveMessage(player))
            return true;
        return messageType == MessageType.SYSTEM && canReceiveCommand(player);
    }

    /**
     * Sends a message to a player, respecting their chat settings.
     * @param player the player
     * @param message message to receive
     * @param messageType type of the message
     * @return if the message has been successfully received
     */
    boolean sendMessage(Player player, Component message, MessageType messageType);

    /**
     * Sends default message send rejection message to player.
     * @param player the player
     */
    void sendRejectionMessage(Player player);

}
