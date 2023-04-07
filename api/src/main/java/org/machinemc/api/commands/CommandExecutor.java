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

    default void sendMessage(final String message) {
        sendMessage(ChatUtils.stringToComponent(message));
    }

    default void sendMessage(final Component message) {
        sendMessage(null, message);
    }

    default void sendMessage(final @Nullable UUID sender, final String message) {
        sendMessage(sender, ChatUtils.stringToComponent(message));
    }

    default void sendMessage(final @Nullable UUID sender, final Component message) {
        sendMessage(sender, message, MessageType.SYSTEM);
    }

    void sendMessage(final @Nullable UUID sender, final Component message, final MessageType type);

    /**
     * Formats the input as a Minecraft command - removes all leading,
     * trailing and double spaces.
     * @param input command input
     * @return formatted command input
     */
    static String formatCommandInput(String input) {
        if(input.isBlank()) return "";
        input = input.trim();
        while(input.contains("  "))
            input = input.replace("  ", " ");
        return input;
    }

}
