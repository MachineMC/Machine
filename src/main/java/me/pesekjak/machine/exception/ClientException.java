package me.pesekjak.machine.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.network.ClientConnection;

@RequiredArgsConstructor
@Getter
public class ClientException extends RuntimeException {

    private final ClientConnection connection;
    private final Exception exception;

}
