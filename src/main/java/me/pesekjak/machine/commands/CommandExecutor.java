package me.pesekjak.machine.commands;

public interface CommandExecutor {

    int execute(String input);

    static String formatCommandInput(String input) {
        if(input.length() == 0) return "";
        input = input.trim();
        while(input.contains("  "))
            input = input.replace("  ", " ");
        return input;
    }

}
