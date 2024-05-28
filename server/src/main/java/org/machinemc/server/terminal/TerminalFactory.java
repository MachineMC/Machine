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
package org.machinemc.server.terminal;

import org.machinemc.server.Machine;
import org.machinemc.server.terminal.dumb.DumbTerminal;
import org.machinemc.server.terminal.smart.SmartCompleter;
import org.machinemc.server.terminal.smart.SmartHighlighter;
import org.machinemc.server.terminal.smart.SmartTerminal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.TerminalBuilder;
import org.machinemc.server.server.Argument;
import org.machinemc.server.logging.FormattedOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

@Getter @Setter
@RequiredArgsConstructor(staticName = "create")
public final class TerminalFactory {

    private final Machine machine;

    /**
     * Creates new application terminal.
     *
     * @param arguments application arguments
     * @return new terminal
     */
    public ApplicationTerminal build(final Set<Argument> arguments) throws IOException {

        final boolean colors = !arguments.contains(Argument.NO_COLORS);

        final ApplicationTerminal terminal;

        if (System.console() == null) {
            terminal = new DumbTerminal(machine, colors, TerminalBuilder.builder()
                    .dumb(true)
                    .system(false)
                    .streams(System.in, System.out)
                    .build(),
                    System.in, System.out);
        } else {
            final SmartTerminal smartTerminal = new SmartTerminal(
                    machine,
                    colors,
                    TerminalBuilder.terminal(),
                    System.in,
                    System.out
            );

            smartTerminal.setCompleter(new SmartCompleter(smartTerminal));
            smartTerminal.setHighlighter(new SmartHighlighter(smartTerminal));
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
