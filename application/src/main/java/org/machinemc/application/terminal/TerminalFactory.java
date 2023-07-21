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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.TerminalBuilder;
import org.machinemc.application.Argument;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.RunnableServer;
import org.machinemc.application.ServerContainer;
import org.machinemc.application.terminal.dumb.DumbTerminal;
import org.machinemc.application.terminal.simple.SimpleTerminal;
import org.machinemc.application.terminal.smart.SmartCompleter;
import org.machinemc.application.terminal.smart.SmartHighlighter;
import org.machinemc.application.terminal.smart.SmartTerminal;
import org.machinemc.server.logging.FormattedOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;
import java.util.function.Supplier;

@Getter @Setter
@RequiredArgsConstructor(staticName = "create")
public final class TerminalFactory {

    private final MachineApplication application;

    /**
     * Creates new application terminal.
     * @return new terminal
     */
    public ApplicationTerminal build() throws IOException {

        final Set<Argument> arguments = application.getArguments();

        final boolean colors = !arguments.contains(Argument.NO_COLORS);

        if (!arguments.contains(Argument.SMART_TERMINAL)) {
            return new SimpleTerminal(application, colors, System.in, System.out);
        }

        final ApplicationTerminal terminal;

        if (System.console() == null) {
            terminal = new DumbTerminal(application, colors, TerminalBuilder.builder()
                    .dumb(true)
                    .system(false)
                    .streams(System.in, System.out)
                    .build(),
                    System.in, System.out);
        } else {
            final SmartTerminal smartTerminal = new SmartTerminal(
                    application,
                    colors,
                    TerminalBuilder.terminal(),
                    System.in,
                    System.out
            );
            final Supplier<RunnableServer> supplier = () ->
                    smartTerminal.getCurrent().flatMap(ServerContainer::getInstance).orElse(null);

            smartTerminal.setCompleter(new SmartCompleter(smartTerminal, supplier));
            smartTerminal.setHighlighter(new SmartHighlighter(smartTerminal, supplier));
            smartTerminal.setHistory(new DefaultHistory());

            terminal = smartTerminal;
        }

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
