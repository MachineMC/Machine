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
package org.machinemc.api.logging;

import org.jetbrains.annotations.Async;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.server.ServerProperty;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Console of the server.
 */
public interface Console extends ServerProperty, CommandExecutor {

    /**
     * Sends a messages to the console at certain logging level.
     * @param level logging level of the message
     * @param messages messages to send
     */
    void log(Level level, String... messages);

    /**
     * Sends multiple messages at INFO logging level.
     * @param messages messages to send
     */
    default void info(String... messages) {
        log(Level.INFO, messages);
    }

    /**
     * Sends multiple objects at INFO logging level.
     * @param objects objects to send
     */
    default void info(Object... objects) {
        info(Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at WARNING logging level.
     * @param messages messages to send
     */
    default void warning(String... messages) {
        log(Level.WARNING, messages);
    }

    /**
     * Sends multiple objects at WARNING logging level.
     * @param objects objects to send
     */
    default void warning(Object... objects) {
        warning(Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at SEVERE logging level.
     * @param messages messages to send
     */
    default void severe(String... messages) {
        log(Level.SEVERE, messages);
    }

    /**
     * Sends multiple objects at SEVERE logging level.
     * @param objects objects to send
     */
    default void severe(Object... objects) {
        severe(Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at CONFIG logging level.
     * @param messages messages to send
     */
    default void config(String... messages) {
        log(Level.CONFIG, messages);
    }

    /**
     * Sends multiple objects at CONFIG logging level.
     * @param objects objects to send
     */
    default void config(Object... objects) {
        config(Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Starts the console command line.
     */
    @Async.Execute
    void start();

    /**
     * Stops the console command line.
     */
    void stop();

    /**
     * Get the input stream used for the console
     * @return input stream
     */
    InputStream getInputStream();


    /**
     * Get the output stream used for the console
     * @return output stream
     */
    OutputStream getOutputStream();

}
