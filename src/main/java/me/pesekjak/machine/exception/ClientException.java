package me.pesekjak.machine.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.network.ClientConnection;

@RequiredArgsConstructor
public class ClientException extends RuntimeException {

    @Getter
    private final ClientConnection connection;
    @Getter
    private final Exception exception;

}
