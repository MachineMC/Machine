package me.pesekjak.machine.logging;

import me.pesekjak.machine.commands.CommandExecutor;
import me.pesekjak.machine.server.ServerProperty;
import net.kyori.adventure.audience.Audience;

import java.util.logging.Level;

/**
 * Server console.
 */
public interface Console extends Audience, ServerProperty, CommandExecutor {

    void log(Level level, String... messages);

    void info(String... messages);

    void warning(String... messages);

    void severe(String... messages);

    void config(String... messages);

    void start();

    void stop();

}
