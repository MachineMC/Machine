package me.pesekjak.machine.exception;

import lombok.Getter;
import me.pesekjak.machine.network.ClientConnection;

public class ClientException extends RuntimeException {

    @Getter
    private final ClientConnection connection;

    public ClientException(ClientConnection connection) {
        super();
        this.connection = connection;
    }

    public ClientException(ClientConnection connection, String message) {
        super(message);
        this.connection = connection;
    }

    public ClientException(ClientConnection connection, String message, Throwable cause) {
        super(message, cause);
        this.connection = connection;
    }

    public ClientException(ClientConnection connection, Throwable cause) {
        super(cause);
        this.connection = connection;
    }

}
