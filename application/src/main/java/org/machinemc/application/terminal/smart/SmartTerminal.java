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
package org.machinemc.application.terminal.smart;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.machinemc.api.logging.Console;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.MachineContainer;
import org.machinemc.application.terminal.SwitchTerminal;
import org.machinemc.application.terminal.WrappedConsole;
import org.machinemc.server.logging.DynamicConsole;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;

/**
 * JLine terminal with widgets and key binds.
 */
public class SmartTerminal extends SwitchTerminal {

    @Getter
    private final Terminal terminal;
    private @Nullable LineReader reader;

    @Getter
    private @Nullable SmartCompleter completer;
    @Getter
    private @Nullable SmartHighlighter highlighter;
    @Getter
    private @Nullable History history;

    private final String prompt = "> ";

    public SmartTerminal(final MachineApplication application,
                         final boolean colors,
                         final Terminal terminal,
                         final InputStream in,
                         final OutputStream out) {
        super(application, colors, in, out);
        this.terminal = terminal;
    }

    @Override
    public void refreshHistory(final List<String> history) {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
        if (reader == null)
            history.forEach(terminal.writer()::println);
        else
            history.forEach(reader::printAbove);
    }

    @Override
    public void openServer(final @Nullable MachineContainer container) {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
        super.openServer(container);
    }

    @Override
    public DynamicConsole createConsole(final MachineContainer container) {
        return new WrappedConsole(this);
    }

    @Override
    public void log(final Console source, final Level level, final String... messages) {
        final LineReader reader = this.reader;
        log((console, message) -> {
            if (reader != null) {
                reader.printAbove(message);
            } else
                terminal.writer().println(message);
        }, source, level, messages);
    }

    @Override
    public void start() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
        final LineReaderBuilder builder = LineReaderBuilder.builder().terminal(terminal);
        if (completer != null) builder.completer(completer);
        if (highlighter != null) builder.highlighter(highlighter);
        if (history != null) builder.history(history);
        reader = builder.build();

        info("Started the application using Smart Terminal");
        info("Press [TAB] for automatic tab completing");
        info("Exit to the main application console using [ALT] + [X]");

        Scheduler.task((i, session) -> {
            while (getApplication().isRunning()) {
                try {
                    final String command = reader.readLine(prompt);
                    execute(command);
                } catch (Exception ignored) { }
            }
            return null;
        }).async().run(getApplication().getScheduler());
    }

    @Override
    public void stop() {
        getApplication().shutdown();
    }

    /**
     * Updates terminal's completer.
     * @param completer new completer
     */
    public void setCompleter(final @Nullable SmartCompleter completer) {
        if (reader != null) throw new UnsupportedOperationException();
        this.completer = completer;
    }

    /**
     * Updates terminal's highlighter.
     * @param highlighter new highlighter
     */
    public void setHighlighter(final @Nullable SmartHighlighter highlighter) {
        if (reader != null) throw new UnsupportedOperationException();
        this.highlighter = highlighter;
    }

    /**
     * Updates terminal's history.
     * @param history new history
     */
    public void setHistory(final @Nullable History history) {
        if (reader != null) throw new UnsupportedOperationException();
        this.history = history;
    }

}
