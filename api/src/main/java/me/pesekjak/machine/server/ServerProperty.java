package me.pesekjak.machine.server;

import me.pesekjak.machine.Server;
import me.pesekjak.machine.exception.ExceptionHandler;
import me.pesekjak.machine.file.ServerProperties;
import me.pesekjak.machine.logging.Console;
import org.jetbrains.annotations.NotNull;

/**
 * Indicates that the class is dependent on the server,
 * adds shortcuts to some parts of the server.
 */
public interface ServerProperty {

    /**
     * @return server implementation used in this class
     */
    @NotNull Server getServer();

    /**
     * @return console implementation used by the server
     */
    default @NotNull Console getServerConsole() {
        return getServer().getConsole();
    }

    /**
     * @return exception handler used by the server
     */
    default @NotNull ExceptionHandler getServerExceptionHandler() {
        return getServer().getExceptionHandler();
    }

}
