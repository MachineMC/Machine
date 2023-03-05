package org.machinemc.server.logging;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;
import lombok.Setter;
import org.machinemc.server.Machine;
import org.machinemc.api.chat.ChatColor;
import org.machinemc.api.chat.ChatUtils;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.logging.Console;
import org.machinemc.api.server.schedule.Scheduler;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.*;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import static org.machinemc.api.chat.ChatUtils.asciiColor;

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
    private @Nullable TextColor
            configColor = null,
            infoColor = null,
            warningColor = ChatColor.GOLD.asStyle().color(),
            severeColor = ChatColor.RED.asStyle().color();

    @Getter @Setter
    private String prompt = "> ";

    public static final String RESET = "\033[0m";
    public static final String EMPTY = "";

    public ServerConsole(Machine server, boolean colors) {
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
    public void log(Level level, String... messages) {
        final String prefix = switch (level.intValue()) {
            case 700 -> (colors && configColor != null ? asciiColor(configColor) : EMPTY) + configPrefix + ": "; // Config value
            case 800 -> (colors && infoColor != null ? asciiColor(infoColor) : EMPTY) + infoPrefix + ": "; // Info value
            case 900 -> (colors && warningColor != null ? asciiColor(warningColor) : EMPTY) + warningPrefix + ": "; // Warning value
            case 1000 -> (colors && severeColor != null ? asciiColor(severeColor) : EMPTY) + severePrefix + ": "; // Severe value
            default -> "";
        };
        final String date = now();
        final LineReader reader = this.reader;
        for(String message : messages) {
            final String formatted = colors ? "[" + date + "] " + prefix + ChatUtils.consoleFormatted(message) + RESET
                    : "[" + date + "] " + prefix + message;
            if(reader != null && running) {
                reader.printAbove(formatted);
            } else
                terminal.writer().println(formatted);
        }
    }

    @Override
    public void sendMessage(final Identity source, final Component message, final MessageType type) {
        if(colors)
            info(ChatUtils.consoleFormatted(message));
        else
            info(ChatUtils.componentToString(message));
    }

    @Override
    public void start() {
        if(running) throw new IllegalStateException("The console is already running");
        running = true;
        reader = LineReaderBuilder.builder()
                .completer(completer)
                .highlighter(highlighter)
                .history(history)
                .terminal(terminal)
                .build();
        final LineReader reader = this.reader;
        if(reader == null) return;
        Scheduler.task((i, session) -> {
            while(running) {
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

    /**
     * Returns a current date using console's date formatter.
     * @return formatted date
     */
    private String now() {
        return dateFormatter != null ? dateFormatter.format(LocalDateTime.now()) : EMPTY;
    }

}
