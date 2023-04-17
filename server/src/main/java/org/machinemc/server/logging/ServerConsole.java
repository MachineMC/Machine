package org.machinemc.server.logging;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.*;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.logging.Console;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.util.ChatUtils;
import org.machinemc.server.Machine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import static org.machinemc.scriptive.util.ChatUtils.asciiColor;

/**
 * Represents default console implementation with colors support, tab
 * completion, highlighter and history.
 */
public class ServerConsole implements Console {

    @Getter
    private final Machine server;
    @Getter @Setter
    private boolean colors;

    final Terminal terminal;
    private volatile @Nullable LineReader reader;

    @Getter
    private final Completer completer;
    @Getter
    private final Highlighter highlighter;
    @Getter
    private final History history;

    private volatile boolean running = false;

    @Getter @Setter
    private @Nullable DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    @Getter @Setter
    private String
            configPrefix = "CONFIG",
            infoPrefix = "INFO",
            warningPrefix = "WARNING",
            severePrefix = "SEVERE";
    @Getter @Setter
    private @Nullable Colour
            configColor = null,
            infoColor = null,
            warningColor = ChatColor.GOLD,
            severeColor = ChatColor.RED;

    @Getter @Setter
    private String prompt = "> ";

    public static final String RESET = "\033[0m";
    public static final String EMPTY = "";

    public ServerConsole(final Machine server, final boolean colors) {
        this.server = server;
        this.colors = colors;
        try {
            terminal = TerminalBuilder.terminal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        completer = new ConsoleCompleter(server, this);
        highlighter = new ConsoleHighlighter(server, this);
        history = new DefaultHistory();
    }

    @Override
    public void log(final Level level, final String... messages) {
        final String prefix = switch (level.intValue()) {
            case 700 -> (colors && configColor != null ? asciiColor(configColor) : EMPTY)
                    + configPrefix + ": "; // Config value
            case 800 -> (colors && infoColor != null ? asciiColor(infoColor) : EMPTY)
                    + infoPrefix + ": "; // Info value
            case 900 -> (colors && warningColor != null ? asciiColor(warningColor) : EMPTY)
                    + warningPrefix + ": "; // Warning value
            case 1000 -> (colors && severeColor != null ? asciiColor(severeColor) : EMPTY)
                    + severePrefix + ": "; // Severe value
            default -> "";
        };
        final String date = now();
        final LineReader reader = this.reader;
        for (String message : messages) {
            final String formatted = colors ? "[" + date + "] " + prefix + ChatUtils.consoleFormatted(message) + RESET
                    : "[" + date + "] " + prefix + message;
            if (reader != null && running) {
                reader.printAbove(formatted);
            } else
                terminal.writer().println(formatted);
        }
    }

    /**
     * Sends a message to the console.
     * @param message message to send
     */
    public void sendMessage(final Component message) {
        if (colors)
            info(ChatUtils.consoleFormatted(message));
        else
            info(message.toLegacyString());
    }

    @Override
    public void start() {
        if (running) throw new IllegalStateException("The console is already running");
        running = true;
        reader = LineReaderBuilder.builder()
                .completer(completer)
                .highlighter(highlighter)
                .history(history)
                .terminal(terminal)
                .build();
        final LineReader reader = this.reader;
        if (reader == null) return;
        Scheduler.task((i, session) -> {
            while (running) {
                try {
                    final String command = reader.readLine(prompt);
                    execute(command);
                } catch (Exception ignored) { }
            }
            return null;
        }).async().run(server.getScheduler());
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public int execute(final String input) {
        final String formattedInput = CommandExecutor.formatCommandInput(input);
        if (formattedInput.length() == 0) return 0;
        final ParseResults<CommandExecutor> parse = server.getCommandDispatcher().parse(formattedInput, this);
        final String[] parts = formattedInput.split(" ");
        try {
            return server.getCommandDispatcher().execute(parse);
        } catch (CommandSyntaxException exception) {
            if (exception.getCursor() == 0) {
                sendMessage(TextComponent.of("Unknown command '" + parts[0] + "'").modify()
                        .color(ChatColor.RED)
                        .finish());
                return -1;
            }
            sendMessage(TextComponent.of(exception.getRawMessage().getString()).modify()
                    .color(ChatColor.RED)
                    .finish());
            sendMessage(TextComponent.of(formattedInput.substring(0, exception.getCursor()))
                    .append(TextComponent.of(formattedInput.substring(exception.getCursor())).modify()
                            .color(ChatColor.RED)
                            .underlined(true)
                            .finish())
                    .append(TextComponent.of("<--[HERE]").modify()
                            .color(ChatColor.RED)
                            .finish()));
            return -1;
        }
    }

    /**
     * Returns a current date using console's date formatter.
     * @return formatted date
     */
    private String now() {
        return dateFormatter != null ? dateFormatter.format(LocalDateTime.now()) : EMPTY;
    }

}
