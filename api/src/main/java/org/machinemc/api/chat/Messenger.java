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
package org.machinemc.api.chat;

import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.entities.Player;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;

import java.util.Optional;
import java.util.Set;

public interface Messenger extends CodecPart, ServerProperty {

    /**
     * Registers new chat type to the messenger if it's not registered already
     * in a different one.
     * @param chatType chat type to register
     */
    void addChatType(ChatType chatType);

    /**
     * Removed a chat type with given name if it's registered in this messenger.
     * @param name name of the chat type
     * @return if the chat type with given name was successfully removed
     */
    default boolean removeChatType(NamespacedKey name) {
        return getChatType(name).map(this::removeChatType).orElse(false);
    }

    /**
     * Removes the chat type from the messenger if it's registered in this messenger.
     * @param chatType chat type to remove
     * @return if the chat type was successfully removed
     */
    boolean removeChatType(ChatType chatType);

    /**
     * Checks if chat type with given name is registered in
     * the messenger.
     * @param name name of the chat type
     * @return if the chat type with given name is registered in this messenger
     */
    default boolean isRegistered(NamespacedKey name) {
        return getChatType(name).map(this::isRegistered).orElse(false);
    }

    /**
     * Checks if the chat type is registered in this messenger.
     * @param chatType chat type to check
     * @return if the chat type is registered in this messenger
     */
    boolean isRegistered(ChatType chatType);

    /**
     * Returns chat type with the given name registered in this messenger.
     * @param name name of the chat type
     * @return chat type with given name in this messenger
     */
    Optional<ChatType> getChatType(NamespacedKey name);

    /**
     * Returns chat type with given id registered in this messenger.
     * @param id id of the chat type
     * @return chat type with given id in this messenger
     */
    Optional<ChatType> getByID(int id);

    /**
     * Returns the id associated with the given chat type registered in this messenger.
     * @param chatType the chat type
     * @return the id of the chat type, or -1 if it's not registered
     */
    int getChatTypeID(ChatType chatType);

    /**
     * @return unmodifiable set of all dimensions registered in this messenger
     */
    @Unmodifiable Set<ChatType> getChatTypes();

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
     * Sends default message send rejection message to player.
     * @param player the player
     */
    void sendRejectionMessage(Player player);

    /**
     * Returns the NBT compound of the given chat type.
     * @param chatType the chat type
     * @return NBT of the given chat type
     */
    NBTCompound getChatTypeNBT(ChatType chatType);

}
