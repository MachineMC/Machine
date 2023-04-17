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
