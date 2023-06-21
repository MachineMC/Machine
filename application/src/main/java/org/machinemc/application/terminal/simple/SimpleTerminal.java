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
package org.machinemc.application.terminal.simple;

import org.jetbrains.annotations.Nullable;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.PlatformConsole;
import org.machinemc.application.ServerContainer;
import org.machinemc.application.terminal.BaseTerminal;
import org.machinemc.application.terminal.WrappedConsole;
import org.machinemc.server.logging.DynamicConsole;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * Simple terminal that only prints out output from all servers.
 */
public class SimpleTerminal extends BaseTerminal {

    public SimpleTerminal(final MachineApplication application,
                          final boolean colors,
                          final InputStream in,
                          final OutputStream out) {
        super(application, colors, in, out);
    }

    @Override
    public int execute(final String input) {
        return executeApplication(input);
    }

    @Override
    public void openServer(final @Nullable ServerContainer container) {

    }

    @Override
    public void exitServer(final ServerContainer container) {

    }

    @Override
    public DynamicConsole createConsole(final ServerContainer container) {
        return new WrappedConsole(this);
    }

    @Override
    public void log(final @Nullable PlatformConsole source, final Level level, final String... messages) {
        log((console, message) -> System.out.println(message), source, level, messages);
    }

    @Override
    public void start() {
        info("Started the application using Output-Only Terminal");
        info("This terminal is only for logging information, for interactive");
        info("terminal, start the application with 'smart-terminal' argument");
    }

    @Override
    public void stop() {

    }

}
