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

import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.server.Machine;

import java.io.*;
import java.util.logging.Level;

/**
 * Represents a basic console implementation with colors support intended to be compatible with all consoles.
 */
public class SimpleConsole extends BaseConsole {

    private final PrintStream out;
    private final InputStream in;

    public SimpleConsole(final Machine server, final boolean colors, final PrintStream out, final InputStream in) {
        super(server, colors);
        this.out = out;
        this.in = in;
    }

    @Override
    public void start() {
        super.start();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Scheduler.task((i, session) -> {
            while (isRunning()) {
                try {
                    final String command = reader.readLine();
                    execute(command);
                } catch (Exception ignored) { }
            }
            return null;
        }).async().delay(1).run(getServer().getScheduler());
    }

    @Override
    public void log(final Level level, final String... messages) {
        log(out::println, level, messages);
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

}
