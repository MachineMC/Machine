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
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.*;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Level;

import static me.pesekjak.machine.chat.ChatUtils.asciiColor;

public class ServerConsole implements Console {

    @Getter
    private final Machine server;
    @Getter @Setter
    private boolean colors;

    private volatile Terminal terminal;
    private volatile LineReader reader;

    @Getter
    private volatile Completer completer;
    @Getter
    private volatile Highlighter highlighter;
    @Getter
    private volatile History history;

    private volatile boolean running = false;

    @Getter @Setter
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    @Getter @Setter @NotNull
    private String
            configPrefix = "CONFIG",
            infoPrefix = "INFO",
            warningPrefix = "WARNING",
            severePrefix = "SEVERE";
    @Getter @Setter @Nullable
    private TextColor
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
    public void log(@NotNull Level level, String... messages) {
        final String prefix = switch (level.intValue()) {
            case 700 -> (colors ? asciiColor(configColor) : EMPTY) + configPrefix + ": "; // Config value
            case 800 -> (colors ? asciiColor(infoColor) : EMPTY) + infoPrefix + ": "; // Info value
            case 900 -> (colors ? asciiColor(warningColor) : EMPTY) + warningPrefix + ": "; // Warning value
            case 1000 -> (colors ? asciiColor(severeColor) : EMPTY) + severePrefix + ": "; // Severe value
            default -> "";
        };
        final String date = now();
        for(String message : messages) {
            final String formatted = colors ? "[" + date + "] " + prefix + ChatUtils.consoleFormatted(message) + RESET
                    : "[" + date + "] " + prefix + message;
            if(reader != null && running)
                reader.printAbove(formatted);
            else
                terminal.writer().println(formatted);
        }
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
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        if(colors)
            info(ChatUtils.consoleFormatted(message));
        else
            info(ChatUtils.componentToString(message));
    }

    @Override
    public void start() {
        if(server.getScheduler() == null) throw new IllegalStateException();
        if(running) throw new IllegalStateException("The console is already running");
        running = true;
        reader = LineReaderBuilder.builder()
                .completer(completer)
                .highlighter(highlighter)
                .history(history)
                .terminal(terminal)
                .build();
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
