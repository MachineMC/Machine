package me.pesekjak.machine.logging;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chat.ChatColor;
import me.pesekjak.machine.chat.ChatUtils;
import me.pesekjak.machine.commands.CommandExecutor;
import me.pesekjak.machine.server.schedule.Scheduler;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

public class ServerConsole implements Console {

    @Getter
    private final Machine server;
    @Getter @Setter
    private boolean colors;

    @Getter @Setter
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    @Getter @Setter @NotNull
    private String
            configPrefix = "CONFIG",
            infoPrefix = "INFO",
            warningPrefix = "WARNING",
            severePrefix = "SEVERE",
            configColor = EMPTY,
            infoColor = EMPTY,
            warningColor = YELLOW,
            severeColor = RED;

    @Getter
    private boolean running = false;

    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String
            BLACK = "\033[0;30m",
            RED = "\033[0;31m",
            GREEN = "\033[0;32m",
            YELLOW = "\033[0;33m",
            BLUE = "\033[0;34m",
            PURPLE = "\033[0;35m",
            CYAN = "\033[0;36m",
            WHITE = "\033[0;37m";

    // Bold
    public static final String
            BLACK_BOLD = "\033[1;30m",
            RED_BOLD = "\033[1;31m",
            GREEN_BOLD = "\033[1;32m",
            YELLOW_BOLD = "\033[1;33m",
            BLUE_BOLD = "\033[1;34m",
            PURPLE_BOLD = "\033[1;35m",
            CYAN_BOLD = "\033[1;36m",
            WHITE_BOLD = "\033[1;37m";

    // Underline
    public static final String
            BLACK_UNDERLINED = "\033[4;30m",
            RED_UNDERLINED = "\033[4;31m",
            GREEN_UNDERLINED = "\033[4;32m",
            YELLOW_UNDERLINED = "\033[4;33m",
            BLUE_UNDERLINED = "\033[4;34m",
            PURPLE_UNDERLINED = "\033[4;35m",
            CYAN_UNDERLINED = "\033[4;36m",
            WHITE_UNDERLINED = "\033[4;37m";

    // Background
    public static final String
            BLACK_BACKGROUND = "\033[40m",
            RED_BACKGROUND = "\033[41m",
            GREEN_BACKGROUND = "\033[42m",
            YELLOW_BACKGROUND = "\033[43m",
            BLUE_BACKGROUND = "\033[44m",
            PURPLE_BACKGROUND = "\033[45m",
            CYAN_BACKGROUND = "\033[46m",
            WHITE_BACKGROUND = "\033[47m";

    // High Intensity
    public static final String
            BLACK_BRIGHT = "\033[0;90m",
            RED_BRIGHT = "\033[0;91m",
            GREEN_BRIGHT = "\033[0;92m",
            YELLOW_BRIGHT = "\033[0;93m",
            BLUE_BRIGHT = "\033[0;94m",
            PURPLE_BRIGHT = "\033[0;95m",
            CYAN_BRIGHT = "\033[0;96m",
            WHITE_BRIGHT = "\033[0;97m";

    // Bold High Intensity
    public static final String
            BLACK_BOLD_BRIGHT = "\033[1;90m",
            RED_BOLD_BRIGHT = "\033[1;91m",
            GREEN_BOLD_BRIGHT = "\033[1;92m",
            YELLOW_BOLD_BRIGHT = "\033[1;93m",
            BLUE_BOLD_BRIGHT = "\033[1;94m",
            PURPLE_BOLD_BRIGHT = "\033[1;95m",
            CYAN_BOLD_BRIGHT = "\033[1;96m",
            WHITE_BOLD_BRIGHT = "\033[1;97m";

    // High Intensity backgrounds
    public static final String
            BLACK_BACKGROUND_BRIGHT = "\033[0;100m",
            RED_BACKGROUND_BRIGHT = "\033[0;101m",
            GREEN_BACKGROUND_BRIGHT = "\033[0;102m",
            YELLOW_BACKGROUND_BRIGHT = "\033[0;103m",
            BLUE_BACKGROUND_BRIGHT = "\033[0;104m",
            PURPLE_BACKGROUND_BRIGHT = "\033[0;105m",
            CYAN_BACKGROUND_BRIGHT = "\033[0;106m",
            WHITE_BACKGROUND_BRIGHT = "\033[0;107m";

    public static final String EMPTY = "";

    public ServerConsole(Machine server, boolean colors) {
        this.server = server;
        this.colors = colors;
    }

    @Override
    public void log(@NotNull Level level, String... messages) {
        final String prefix = switch (level.intValue()) {
            case 700 -> (colors ? configColor : EMPTY) + configPrefix + ": "; // Config value
            case 800 -> (colors ? infoColor : EMPTY) + infoPrefix + ": "; // Info value
            case 900 -> (colors ? warningColor : EMPTY) + warningPrefix + ": "; // Warning value
            case 1000 -> (colors ? severeColor : EMPTY) + severePrefix + ": "; // Severe value
            default -> "";
        };
        final String date = now();
        for(String message : messages)
            System.out.println(
                    colors ? "[" + date + "] " + prefix + ChatUtils.consoleFormatted(message) + RESET
                            : "[" + date + "] " + prefix + message
            );
    }

    @Override
    public final void info(String... messages) {
        Arrays.stream(messages).forEach(message -> log(Level.INFO, message));
    }

    @Override
    public final void warning(String... messages) {
        Arrays.stream(messages).forEach(message -> log(Level.WARNING, message));
    }

    @Override
    public final void severe(String... messages) {
        Arrays.stream(messages).forEach(message -> log(Level.SEVERE, message));
    }

    @Override
    public void config(String... messages) {
        Arrays.stream(messages).forEach(message -> log(Level.CONFIG, message));
    }

    @Override
    public void start() {
        if(server.getScheduler() == null) throw new IllegalStateException();
        if(running) throw new IllegalStateException("The console is already listening to system input");
        running = true;
        Scheduler.task((i, session) -> {
            final Scanner in = new Scanner(System.in);
            while(running) {
                System.out.print(colors ? ("> " + CYAN) : "> ");
                String input = in.nextLine();
                if(colors) System.out.print(RESET);
                execute(input);
            }
            return null;
        }).async().run(server.getScheduler());
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        if(colors)
            info(ChatUtils.consoleFormatted(message));
        else
            info(ChatUtils.componentToString(message));
    }

    private String now() {
        return dateFormatter != null ? dateFormatter.format(LocalDateTime.now()) : EMPTY;
    }

    @Override
    public int execute(String input) {
        input = CommandExecutor.formatCommandInput(input);
        if(input.length() == 0) return 0;
        final ParseResults<CommandExecutor> parse = server.getCommandDispatcher().parse(input, this);
        final String[] parts = input.split(" ");
        try {
            return server.getCommandDispatcher().execute(parse);
        } catch (CommandSyntaxException exception) {
            if(exception.getCursor() == 0) {
                sendMessage(Component.text("Unknown command '" + parts[0] + "'").style(ChatColor.RED.asStyle()));
                return -1;
            }
            sendMessage(Component.text(exception.getRawMessage().getString()).style(ChatColor.RED.asStyle()));
            sendMessage(Component.text(input.substring(0, exception.getCursor()))
                    .append(Component.text(input.substring(exception.getCursor())).style(ChatColor.RED.asStyle().decorate(TextDecoration.UNDERLINED)))
                    .append(Component.text("<--[HERE]").style(ChatColor.RED.asStyle())));
            return -1;
        }
    }

}
