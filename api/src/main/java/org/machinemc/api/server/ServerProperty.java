package org.machinemc.api.server;

import org.machinemc.server.Server;
import org.machinemc.api.exception.ExceptionHandler;
import org.machinemc.api.logging.Console;

/**
 * Indicates that the class is dependent on the server,
 * adds shortcuts to some parts of the server.
 */
public interface ServerProperty {

    /**
     * @return server implementation used in this class
     */
    Server getServer();

    /**
     * @return console implementation used by the server
     */
    default Console getServerConsole() {
        return getServer().getConsole();
    }

    /**
     * @return exception handler used by the server
     */
    default ExceptionHandler getServerExceptionHandler() {
        return getServer().getExceptionHandler();
    }

}
