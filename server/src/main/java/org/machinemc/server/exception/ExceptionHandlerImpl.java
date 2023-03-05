package org.machinemc.server.exception;

import lombok.Getter;
import org.machinemc.server.Machine;
import org.machinemc.api.exception.ExceptionHandler;
import org.machinemc.api.logging.Console;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Default exception handler implementation.
 */
public class ExceptionHandlerImpl implements ExceptionHandler, ServerProperty {

    @Getter
    private final @NotNull Machine server;
    private final @NotNull Console console;

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
        while(throwable.getCause() != null)
            throwable = throwable.getCause();
        console.severe(server + " generated " + throwable.getClass().getName(),
                "Reason: " + (reason != null ? reason : throwable.getMessage()),
                "Stack trace: ");
        console.severe(Arrays.stream(throwable.getStackTrace()).map(Object::toString).toArray(String[]::new));
    }

    /**
     * Handles client exception specifically.
     * @param exception client exception to handle
     */
    protected void handle(@NotNull ClientException exception) {
        final ClientConnection connection = exception.getConnection();
        Throwable throwable = exception;
        while(throwable.getCause() != null)
            throwable = throwable.getCause();
        server.getConsole().severe("Client generated " + throwable.getClass().getName(),
                "Login username: " + connection.getLoginUsername(),
                "Address: " + connection.getClientSocket().getInetAddress(),
                "Reason: " + exception.getMessage(),
                "Stack trace:"
        );
        console.severe(Arrays.stream(throwable.getStackTrace()).map(Object::toString).toArray(String[]::new));
    }

}
