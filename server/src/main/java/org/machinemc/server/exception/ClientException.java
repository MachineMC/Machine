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
package org.machinemc.server.exception;

import lombok.Getter;
import org.machinemc.server.network.ClientConnection;

/**
 * Represents an exception generated by client while being on the server.
 */
public class ClientException extends RuntimeException {

    @Getter
    private final ClientConnection connection;

    public ClientException(final ClientConnection connection) {
        super();
        this.connection = connection;
    }

    public ClientException(final ClientConnection connection, final String message) {
        super(message);
        this.connection = connection;
    }

    public ClientException(final ClientConnection connection, final String message, final Throwable cause) {
        super(message, cause);
        this.connection = connection;
    }

    public ClientException(final ClientConnection connection, final Throwable cause) {
        super(cause);
        this.connection = connection;
    }

}
