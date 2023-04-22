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
 * If not, see <https://www.gnu.org/licenses/>.
 */
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
