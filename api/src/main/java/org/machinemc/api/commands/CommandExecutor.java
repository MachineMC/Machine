package org.machinemc.api.commands;

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
