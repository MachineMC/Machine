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
package org.machinemc.application.terminal;

import lombok.AllArgsConstructor;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.TerminalBuilder;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.RunnableServer;
import org.machinemc.application.ServerContainer;
import org.machinemc.application.terminal.smart.SmartCompleter;
import org.machinemc.application.terminal.smart.SmartHighlighter;
import org.machinemc.application.terminal.smart.SmartTerminal;
import org.machinemc.server.logging.FormattedOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
@AllArgsConstructor(staticName = "create")
public final class TerminalFactory {

    private final MachineApplication application;

    /**
     * Creates new application terminal.
     * @return new terminal
     */
    public ApplicationTerminal build() throws IOException {

        final SmartTerminal terminal = new SmartTerminal(
                application,
                true,
                TerminalBuilder.terminal(),
                System.in,
                System.out
        );

        final Supplier<RunnableServer> supplier = () -> {
            final ServerContainer container = terminal.getCurrent();
            if (container == null) return null;
            return container.getInstance();
        };

        terminal.setCompleter(new SmartCompleter(terminal, supplier));
        terminal.setHighlighter(new SmartHighlighter(terminal, supplier));
        terminal.setHistory(new DefaultHistory());

        System.setOut(new PrintStream(new FormattedOutputStream(
                terminal::info,
                terminal.getOutputStream(),
                "[stdout] "
        )));
        System.setErr(new PrintStream(new FormattedOutputStream(
                terminal::severe,
                terminal.getOutputStream(),
                "[stderr] "
        )));

        return terminal;
    }

}
