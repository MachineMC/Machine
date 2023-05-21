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

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.logging.Console;
import org.machinemc.application.MachineApplication;
import org.machinemc.application.MachineContainer;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.logging.DynamicConsole;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Terminal for Machine application.
 */
public interface ApplicationTerminal extends CommandExecutor {

    /**
     * @return application of the terminal
     */
    MachineApplication getApplication();

    /**
     * Called when new server is opened in the terminal.
     * <p>
     * Terminal should switch over console of the provided server.
     * @param container container
     */
    void openServer(@Nullable MachineContainer container);

    /**
     * Called when user exits from the opened server.
     * @param container container the user exit from
     */
    void exitServer(MachineContainer container);

    /**
     * Creates new console implementation for this terminal for
     * provided server.
     * <p>
     * Keep in mind that in this phase the container has no active
     * server instance.
     * @param container container to create the console for
     * @return new console
     */
    DynamicConsole createConsole(MachineContainer container);

    /**
     * @return whether the terminal display colors.
     */
    boolean isColored();

    /**
     * Changes whether the terminal displays colors.
     * <p>
     * Some terminals might now support colors.
     * @param colors if the colors should be displayed
     */
    void setColors(boolean colors);

    /**
     * Sends a messages to the console at certain logging level.
     * @param source source of the message
     * @param level logging level of the message
     * @param messages messages to send
     */
    void log(@Nullable Console source, Level level, String... messages);

    /**
     * Sends multiple messages at INFO logging level.
     * @param source source of the message
     * @param messages messages to send
     */
    default void info(@Nullable Console source, String... messages) {
        log(source, Level.INFO, messages);
    }

    /**
     * Sends multiple objects at INFO logging level.
     * @param source source of the message
     * @param objects objects to send
     */
    default void info(@Nullable Console source, Object... objects) {
        info(source, Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at INFO logging level with application as the message source.
     * @param messages messages to send
     */
    default void info(String... messages) {
        info(null, messages);
    }

    /**
     * Sends multiple objects at INFO logging level with application as the message source.
     * @param objects objects to send
     */
    default void info(Object... objects) {
        info(null, objects);
    }

    /**
     * Sends multiple messages at WARNING logging level.
     * @param source source of the message
     * @param messages messages to send
     */
    default void warning(@Nullable Console source, String... messages) {
        log(source, Level.WARNING, messages);
    }

    /**
     * Sends multiple objects at WARNING logging level.
     * @param source source of the message
     * @param objects objects to send
     */
    default void warning(@Nullable Console source, Object... objects) {
        warning(source, Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at WARNING logging level with application as the message source.
     * @param messages messages to send
     */
    default void warning(String... messages) {
        warning(null, messages);
    }

    /**
     * Sends multiple objects at WARNING logging level with application as the message source.
     * @param objects objects to send
     */
    default void warning(Object... objects) {
        warning(null, objects);
    }

    /**
     * Sends multiple messages at SEVERE logging level.
     * @param source source of the message
     * @param messages messages to send
     */
    default void severe(@Nullable Console source, String... messages) {
        log(source, Level.SEVERE, messages);
    }

    /**
     * Sends multiple objects at SEVERE logging level.
     * @param source source of the message
     * @param objects objects to send
     */
    default void severe(@Nullable Console source, Object... objects) {
        severe(source, Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at SEVERE logging level with application as the message source.
     * @param messages messages to send
     */
    default void severe(String... messages) {
        severe(null, messages);
    }

    /**
     * Sends multiple objects at SEVERE logging level with application as the message source.
     * @param objects objects to send
     */
    default void severe(Object... objects) {
        severe(null, objects);
    }

    /**
     * Sends multiple messages at CONFIG logging level.
     * @param source source of the message
     * @param messages messages to send
     */
    default void config(@Nullable Console source, String... messages) {
        log(source, Level.CONFIG, messages);
    }

    /**
     * Sends multiple objects at CONFIG logging level.
     * @param source source of the message
     * @param objects objects to send
     */
    default void config(@Nullable Console source, Object... objects) {
        config(source, Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at CONFIG logging level with application as the message source.
     * @param messages messages to send
     */
    default void config(String... messages) {
        config(null, messages);
    }

    /**
     * Sends multiple objects at CONFIG logging level with application as the message source.
     * @param objects objects to send
     */
    default void config(Object... objects) {
        config(null, objects);
    }

    /**
     * Sends component to the terminal.
     * @param source source of the message
     * @param sender uuid of sender
     * @param message component to send
     * @param type type of the message
     */
    void sendMessage(@Nullable Console source, @Nullable UUID sender, Component message, MessageType type);

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
