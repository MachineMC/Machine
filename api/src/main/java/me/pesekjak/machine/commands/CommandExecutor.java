package me.pesekjak.machine.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

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
    @Range(from = -1, to = Integer.MAX_VALUE) int execute(@NotNull String input);

    /**
     * Formats the input as a Minecraft command - removes all leading,
     * trailing and double spaces.
     * @param input command input
     * @return formatted command input
     */
    static @NotNull String formatCommandInput(@NotNull String input) {
        if(input.isBlank()) return "";
        input = input.trim();
        while(input.contains("  "))
            input = input.replace("  ", " ");
        return input;
    }

}
