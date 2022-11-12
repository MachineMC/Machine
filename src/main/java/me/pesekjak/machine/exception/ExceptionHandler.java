package me.pesekjak.machine.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.logging.ServerConsole;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.server.ServerProperty;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class ExceptionHandler implements ServerProperty {

    @Getter
    private final Machine server;

    public void handle(ClientException exception) {
        final ClientConnection connection = exception.getConnection();
        server.getConsole().severe("Client generated unhandled exception: " + exception.getClass().getName(),
                "Login username: " + connection.getLoginUsername(),
                "Address: " + connection.getClientSocket().getInetAddress(),
                "Stack trace:"
        );
        System.out.print(ServerConsole.RED);
        exception.getException().printStackTrace();
        System.out.print(ServerConsole.RESET);
    }

}
