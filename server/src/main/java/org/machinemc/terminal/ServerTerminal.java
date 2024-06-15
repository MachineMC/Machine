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
package org.machinemc.terminal;

import lombok.Getter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.machinemc.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a server terminal.
 * <p>
 * Only one server terminal can exist at the time.
 */
@Getter
public final class ServerTerminal {

    private static final AtomicReference<ServerTerminal> INSTANCE = new AtomicReference<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(Machine.class);

    private final Terminal terminal;
    private final LineReader lineReader;

    /**
     * Returns the active server terminal or initializes a new one
     * in case there is none.
     *
     * @return server terminal
     */
    public static ServerTerminal get() {
        ServerTerminal terminal;
        if ((terminal = INSTANCE.get()) != null) return terminal;
        try {
            terminal = new ServerTerminal();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        INSTANCE.set(terminal);
        return terminal;
    }

    /**
     * Returns the main server logger.
     * <p>
     * This logger has no prefix and should be used to log all messages
     * that come directly from the server.
     *
     * @return server logger
     */
    public static Logger logger() {
        return LOGGER;
    }

    private ServerTerminal() throws IOException {
        injectJULogger();

        final TerminalBuilder builder = TerminalBuilder.builder();

        if (System.console() == null) // if there is no console, the environment does not offer real terminal
            builder.dumb(true)
                    .system(false)
                    .streams(System.in, System.out);

        terminal = builder.build();
        lineReader = LineReaderBuilder.builder().terminal(terminal).build();

        injectStreams();
    }

    /**
     * Removes existing handlers attached to j.u.l root logger and
     * adds SLF4JBridgeHandler to j.u.l's root logger.
     * <p>
     * This effectively translates all j.u.l calls to SLF4J.
     */
    private void injectJULogger() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Changes System out streams to use the proper formatting.
     */
    private void injectStreams() {
        System.setOut(new PrintStream(new FormattedOutputStream(
                s -> logger().info(s),
                getTerminal().output(),
                "[stdout] "
        )));
        System.setErr(new PrintStream(new FormattedOutputStream(
                s -> logger().error(s),
                getTerminal().output(),
                "[stderr] "
        )));
    }

}
