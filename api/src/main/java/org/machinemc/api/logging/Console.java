package org.machinemc.api.logging;

import org.machinemc.api.chat.ChatUtils;
import org.machinemc.api.commands.CommandExecutor;
import org.machinemc.api.server.ServerProperty;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Async;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Console of the server.
 */
public interface Console extends Audience, ServerProperty, CommandExecutor {

    /**
     * Sends a messages to the console at certain logging level.
     * @param level logging level of the message
     * @param messages messages to send
     */
    void log(Level level, String... messages);

    /**
     * Sends multiple messages at INFO logging level
     * @param messages messages to send
     */
    default void info(String... messages) {
        log(Level.INFO, messages);
    }

    /**
     * Sends multiple objects at INFO logging level
     * @param objects objects to send
     */
    default void info(Object... objects) {
        info(Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at WARNING logging level
     * @param messages messages to send
     */
    default void warning(String... messages) {
        log(Level.WARNING, messages);
    }

    /**
     * Sends multiple objects at WARNING logging level
     * @param objects objects to send
     */
    default void warning(Object... objects) {
        warning(Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at SEVERE logging level
     * @param messages messages to send
     */
    default void severe(String... messages) {
        log(Level.SEVERE, messages);
    }

    /**
     * Sends multiple objects at SEVERE logging level
     * @param objects objects to send
     */
    default void severe(Object... objects) {
        severe(Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
    }

    /**
     * Sends multiple messages at CONFIG logging level
     * @param messages messages to send
     */
    default void config(String... messages) {
        log(Level.CONFIG, messages);
    }

    /**
     * Sends multiple objects at CONFIG logging level
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

    default void sendMessage(final Identity source, final Component message, final MessageType type) {
        info(ChatUtils.componentToString(message));
    }

}
