package me.pesekjak.machine.commands;

public interface CommandExecutor {

    int execute(String input);

    static String formatCommandInput(String input) {
        if(input.isBlank()) return "";
        input = input.trim();
        while(input.contains("  "))
            input = input.replace("  ", " ");
        return input;
    }

}
