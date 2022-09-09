package me.pesekjak.machine.logging;

import me.pesekjak.machine.server.ServerProperty;
import net.kyori.adventure.audience.Audience;

import java.util.logging.Level;

public interface IConsole extends Audience, ServerProperty {

    void log(Level level, String... messages);

    void info(String... messages);

    void warning(String... messages);

    void severe(String... messages);

}
