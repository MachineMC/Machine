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
package org.machinemc.api.commands;

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.MessageType;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.util.ChatUtils;

import java.util.UUID;

/**
 * Represents a class which contains a single method for executing commands.
 */
public interface CommandExecutor {

    /**
     * Executes the given command, returning a numeric result
     * from a "command" that was performed, -1 if the operation was
     * unsuccessful.
     * @param input command to execute
     * @return numeric result from the command
     */
    int execute(String input);

    /**
     * Sends a message to the command executor.
     * @param message message to send
     */
    default void sendMessage(final String message) {
        sendMessage(ChatUtils.stringToComponent(message));
    }

    /**
     * Sends a message to the command executor.
     * @param message message to send
     */
    default void sendMessage(final Component message) {
        sendMessage(null, message);
    }

    /**
     * Sends a message to the command executor.
     * @param sender sender uuid
     * @param message message to send
     */
    default void sendMessage(final @Nullable UUID sender, final String message) {
        sendMessage(sender, ChatUtils.stringToComponent(message));
    }

    /**
     * Sends a message to the command executor.
     * @param sender sender uuid
     * @param message message to send
     */
    default void sendMessage(final @Nullable UUID sender, final Component message) {
        sendMessage(sender, message, MessageType.SYSTEM);
    }

    /**
     * Sends a message to the command executor.
     * @param sender sender uuid
     * @param message message to send
     * @param type type of the message
     */
    void sendMessage(@Nullable UUID sender, Component message, MessageType type);

    /**
     * Formats the input as a Minecraft command - removes all leading,
     * trailing and double spaces.
     * @param input command input
     * @return formatted command input
     */
    static String formatCommandInput(String input) {
        if (input.isBlank()) return "";
        input = input.trim();
        while (input.contains("  "))
            input = input.replace("  ", " ");
        return input;
    }

}
