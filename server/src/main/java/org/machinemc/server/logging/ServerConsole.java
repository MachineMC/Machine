package org.machinemc.server.logging;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.*;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.server.Machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Represents default console implementation with colors support, tab
 * completion, highlighter and history.
 */
public class ServerConsole extends BaseConsole {

    private final Terminal terminal;
    private volatile @Nullable LineReader reader;

    @Getter
    private final Completer completer;
    @Getter
    private final Highlighter highlighter;
    @Getter
    private final History history;

    public static final String RESET = "\033[0m";
    public static final String EMPTY = "";

    public ServerConsole(Machine server, boolean colors) {
        super(server, colors);
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
        final LineReader reader = this.reader;
        log(message -> {
            if(reader != null && isRunning()) {
                reader.printAbove(message);
            } else
                terminal.writer().println(message);
        }, level, messages);
    }

    @Override
    public void start() {
        super.start();
        reader = LineReaderBuilder.builder()
                .completer(completer)
                .highlighter(highlighter)
                .history(history)
                .terminal(terminal)
                .build();
        final LineReader reader = this.reader;
        if(reader == null) return;
        Scheduler.task((i, session) -> {
            while(isRunning()) {
                try {
                    final String command = reader.readLine(getPrompt());
                    execute(command);
                } catch (Exception ignored) { }
            }
            return null;
        }).async().run(getServer().getScheduler());
    }

    @Override
    public InputStream getInputStream() {
        return terminal.input();
    }

    @Override
    public OutputStream getOutputStream() {
        return terminal.output();
    }

}
