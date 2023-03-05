package org.machinemc.api.chat;

import org.machinemc.api.entities.Player;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Messenger extends CodecPart, ServerProperty {

    /**
     * Checks if player can receive a message.
     * @param player player to check
     * @return if player can receive a message
     */
    static boolean canReceiveMessage(@NotNull Player player) {
        return player.getChatMode() == ChatMode.ENABLED;
    }

    /**
     * Checks if player can receive a command.
     * @param player player to check
     * @return if player can receive a command
     */
    static boolean canReceiveCommand(@NotNull Player player) {
        return player.getChatMode() != ChatMode.HIDDEN;
    }

    /**
     * Checks if player accepts given message type.
     * @param player player to check
     * @param messageType message type to check
     * @return if player accepts given message type
     */
    static boolean accepts(@NotNull Player player, @NotNull MessageType messageType) {
        if(messageType == MessageType.CHAT && canReceiveMessage(player))
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
    boolean sendMessage(@NotNull Player player, @NotNull Component message, @NotNull MessageType messageType);

    /**
     * Sends default message send rejection message to player.
     * @param player the player
     */
    void sendRejectionMessage(@NotNull Player player);

}
