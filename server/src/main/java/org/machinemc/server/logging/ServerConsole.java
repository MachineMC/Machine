/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.logging;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.*;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.server.Machine;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Represents default console implementation with colors support, tab
 * completion, highlighter and history.
 */
public class ServerConsole extends BaseConsole {

    private Terminal terminal;
    private volatile @Nullable LineReader reader;

    @Getter
    private final Completer completer;
    @Getter
    private final Highlighter highlighter;
    @Getter
    private final History history;

    public static final String RESET = "\033[0m";
    public static final String EMPTY = "";

    private final boolean dumb;

    public ServerConsole(final Machine server, final boolean colors) {
        super(server, colors);

        try {
            if (System.console() != null) {
                terminal = TerminalBuilder.terminal();
                dumb = false;
            } else {
                terminal = TerminalBuilder.builder()
                        .dumb(true)
                        .system(false)
                        .streams(System.in, System.out)
                        .build();
                dumb = true;
                warning("Failed to create classic terminal, created dumb terminal instead");
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        completer = new ConsoleCompleter(server, this);
        highlighter = new ConsoleHighlighter(server, this);
        history = new DefaultHistory();
    }

    @Override
    public void log(final Level level, final String... messages) {
        final LineReader reader = this.reader;
        log(message -> {
            if (reader != null && isRunning()) {
                reader.printAbove(message);
            } else
                terminal.writer().println(message);
        }, level, messages);
    }

    @Override
    public void start() {
        super.start();
        final LineReaderBuilder builder = LineReaderBuilder.builder()
                .terminal(terminal);
        if (!dumb) {
            builder.completer(completer)
                    .highlighter(highlighter)
                    .history(history)
                    .terminal(terminal);
        }
        reader = builder.build();
        final LineReader reader = this.reader;
        if (reader == null) return;
        Scheduler.task((i, session) -> {
            while (isRunning()) {
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
