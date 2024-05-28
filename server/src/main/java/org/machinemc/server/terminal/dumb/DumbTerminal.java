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
package org.machinemc.server.terminal.dumb;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.server.Machine;
import org.machinemc.server.terminal.BaseTerminal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Interactive terminal for not real terminal environments such
 * as IntelliJ terminal.
 */
public class DumbTerminal extends BaseTerminal {

    @Getter
    private final Terminal terminal;
    private @Nullable LineReader reader;

    private final String prompt = "> ";

    public DumbTerminal(final Machine server,
                        final boolean colors,
                        final Terminal terminal,
                        final InputStream in,
                        final OutputStream out) {
        super(server, colors, in, out);
        this.terminal = terminal;
    }

    @Override
    public void log(final Level level, final String... messages) {
        final LineReader reader = this.reader;
        log(message -> {
            if (reader != null)
                reader.printAbove(message);
            else
                terminal.writer().println(message);
        }, level, messages);
    }

    @Override
    public void start() {
        final LineReaderBuilder builder = LineReaderBuilder.builder()
                .terminal(terminal);
        reader = builder.build();

        info("Started the application using Dumb Terminal");
        info("This is most likely caused by running the application");
        info("in not real terminal environment.");

        Scheduler.task((i, session) -> {
            while (server.isRunning()) {
                try {
                    final String command = reader.readLine(prompt);
                    execute(command);
                } catch (Exception exception) {
                    server.getExceptionHandler().handle(exception);
                }
            }
            return null;
        }).async().run(server.getScheduler());
    }

    @Override
    public void stop() {
        server.shutdown();
    }

}
