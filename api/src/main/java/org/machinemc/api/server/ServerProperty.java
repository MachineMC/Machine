package org.machinemc.api.server;

import org.machinemc.server.Server;
import org.machinemc.api.exception.ExceptionHandler;
import org.machinemc.api.logging.Console;
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
