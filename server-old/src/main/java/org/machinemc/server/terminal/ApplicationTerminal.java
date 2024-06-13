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

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.Server;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.logging.Console;
import org.machinemc.scriptive.components.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Terminal for Machine application.
 */
public interface ApplicationTerminal extends CommandExecutor, Console {

    /**
     * @return server attached to the terminal
     */
    Server getServer();

    /**
     * @return whether the terminal display colors.
     */
    boolean isColored();

    /**
     * Changes whether the terminal displays color.
     * <p>
     * Some terminals might now support colors.
     * @param colors if the colors should be displayed
     */
    void setColors(boolean colors);

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
     * Sends multiple messages at WARNING logging level with application as the message source.
     * @param messages messages to send
     */
    default void warning(String... messages) {
        log(Level.WARNING, messages);
    }

    /**
     * Sends multiple objects at WARNING logging level with application as the message source.
     * @param objects objects to send
     */
    default void warning(Object... objects) {
        for (final Object o : objects) warning(String.valueOf(o));
    }

    /**
     * Sends multiple messages at SEVERE logging level with application as the message source.
     * @param messages messages to send
     */
    default void severe(String... messages) {
        log(Level.SEVERE, messages);
    }

    /**
     * Sends multiple objects at SEVERE logging level with application as the message source.
     * @param objects objects to send
     */
    default void severe(Object... objects) {
        for (final Object o : objects) severe(String.valueOf(o));
    }

    /**
     * Sends multiple messages at CONFIG logging level with application as the message source.
     * @param messages messages to send
     */
    default void config(String... messages) {
        log(Level.CONFIG, messages);
    }

    /**
     * Sends multiple objects at CONFIG logging level with application as the message source.
     * @param objects objects to send
     */
    default void config(Object... objects) {
        for (final Object o : objects) config(String.valueOf(o));
    }

    /**
     * Sends component to the terminal.
     * @param sender uuid of sender
     * @param message component to send
     * @param type type of the message
     */
    void sendMessage(@Nullable UUID sender, Component message, MessageType type);

    /**
     * Starts the console command line.
     */
    void start();

    /**
     * Stops the console command line.
     */
    void stop();

    /**
     * @return input stream of the console
     */
    InputStream getInputStream();

    /**
     * @return output stream of the console
     */
    OutputStream getOutputStream();

}
