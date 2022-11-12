package me.pesekjak.machine.commands;

public interface CommandExecutor {

    int execute(String input);

    static String formatCommandInput(String input) {
        if(input.length() == 0) return "";
        while(input.contains("  "))
            input = input.replace("  ", " ");
        while(input.charAt(input.length() - 1) == ' ') {
            input = input.substring(0, input.length() - 1);
            if(input.length() == 0) return "";
        }
        while(input.charAt(0) == ' ') {
            input = input.substring(1);
            if(input.length() == 0) return "";
        }
        return input;
    }

}
