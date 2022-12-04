package me.pesekjak.machine.logging;

import me.pesekjak.machine.chat.ChatUtils;
import me.pesekjak.machine.commands.CommandExecutor;
import me.pesekjak.machine.server.ServerProperty;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NotNull;

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
    void log(@NotNull Level level, String... messages);

    /**
     * Sends multiple messages at INFO logging level
     * @param messages messages to send
     */
    default void info(String... messages) {
        Arrays.stream(messages).forEach(message -> log(Level.INFO, message));
    }

    /**
     * Sends multiple messages at WARNING logging level
     * @param messages messages to send
     */
    default void warning(String... messages) {
        Arrays.stream(messages).forEach(message -> log(Level.WARNING, message));
    }

    /**
     * Sends multiple messages at SEVERE logging level
     * @param messages messages to send
     */
    default void severe(String... messages) {
        Arrays.stream(messages).forEach(message -> log(Level.SEVERE, message));
    }

    /**
     * Sends multiple messages at CONFIG logging level
     * @param messages messages to send
     */
    default void config(String... messages) {
        Arrays.stream(messages).forEach(message -> log(Level.CONFIG, message));
    }

    /**
     * Starts the console command line.
     */
    @Async.Execute
    @NonBlocking
    void start();

    /**
     * Stops the console command line.
     */
    @NonBlocking
    void stop();

    default void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        info(ChatUtils.componentToString(message));
    }

}
