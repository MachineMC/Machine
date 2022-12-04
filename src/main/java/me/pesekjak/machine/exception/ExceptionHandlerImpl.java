package me.pesekjak.machine.exception;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.logging.Console;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.server.ServerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExceptionHandlerImpl implements ExceptionHandler, ServerProperty {

    @Getter
    private final Machine server;
    private final Console console;

    public ExceptionHandlerImpl(@NotNull Machine server) {
        this.server = server;
        this.console = server.getConsole();
    }

    @Override
    public void handle(@NotNull Throwable throwable) {
        handle(throwable, null);
    }

    @Override
    public void handle(@NotNull Throwable throwable, @Nullable String reason) {
        if(throwable instanceof ClientException clientException) {
            handle(clientException);
            return;
        }
        if(throwable instanceof RuntimeException runtimeException && runtimeException.getCause() != null)
            throwable = runtimeException.getCause();
        console.severe(server + " generated " + throwable.getClass().getName(),
                "Reason: " + (reason != null ? reason : throwable.getMessage()),
                "Stack trace: ");
        console.severe(Arrays.stream(throwable.getStackTrace()).map(Object::toString).toArray(String[]::new));
    }

    protected void handle(@NotNull ClientException exception) {
        final ClientConnection connection = exception.getConnection();
        final Throwable throwable;
        if(exception.getCause() != null)
            throwable = exception.getCause();
        else
            throwable = exception;
        server.getConsole().severe("Client generated " + throwable.getClass().getName(),
                "Login username: " + connection.getLoginUsername(),
                "Address: " + connection.getClientSocket().getInetAddress(),
                "Reason: " + exception.getMessage(),
                "Stack trace:"
        );
        console.severe(Arrays.stream(throwable.getStackTrace()).map(Object::toString).toArray(String[]::new));
    }

}
