package me.pesekjak.machine.exception;

import lombok.experimental.UtilityClass;
import me.pesekjak.machine.logging.Console;
import me.pesekjak.machine.network.ClientConnection;

@UtilityClass
public class ExceptionHandler {

    public static void handle(ClientException exception) {
        final ClientConnection connection = exception.getConnection();
        connection.getServer().getConsole().severe("Client generated unhandled exception: " + exception.getClass().getName(),
                "Login username: " + connection.getLoginUsername(),
                "Address: " + connection.getClientSocket().getInetAddress(),
                "Stack trace:"
        );
        System.out.print(Console.RED);
        exception.getException().printStackTrace();
        System.out.print(Console.RESET);
    }

}
