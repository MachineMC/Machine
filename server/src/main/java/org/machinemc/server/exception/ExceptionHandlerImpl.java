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
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.exception;

import lombok.Getter;
import org.machinemc.server.Machine;
import org.machinemc.api.exception.ExceptionHandler;
import org.machinemc.api.logging.Console;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Default exception handler implementation.
 */
public class ExceptionHandlerImpl implements ExceptionHandler, ServerProperty {

    @Getter
    private final Machine server;
    private final Console console;

    public ExceptionHandlerImpl(final Machine server) {
        this.server = server;
        this.console = server.getConsole();
    }

    @Override
    public void handle(final Throwable throwable) {
        handle(throwable, null);
    }

    @Override
    public void handle(final Throwable throwable, final @Nullable String reason) {
        if (throwable instanceof ClientException clientException) {
            handle(clientException);
            return;
        }
        Throwable initialCause = throwable;
        while (initialCause.getCause() != null)
            initialCause = initialCause.getCause();
        console.severe(server + " generated " + initialCause.getClass().getName(),
                "Reason: " + (reason != null ? reason : initialCause.getMessage()),
                "Stack trace: ");
        console.severe(Arrays.stream(initialCause.getStackTrace()).map(Object::toString).toArray(String[]::new));
    }

    /**
     * Handles client exception specifically.
     * @param exception client exception to handle
     */
    protected void handle(final ClientException exception) {
        final ClientConnection connection = exception.getConnection();
        Throwable throwable = exception;
        while (throwable.getCause() != null)
            throwable = throwable.getCause();
        server.getConsole().severe("Client generated " + throwable.getClass().getName(),
                "Login username: " + connection.getLoginUsername(),
                "Address: " + connection.getAddress(),
                "Reason: " + exception.getMessage(),
                "Stack trace:"
        );
        console.severe(Arrays.stream(throwable.getStackTrace()).map(Object::toString).toArray(String[]::new));
    }

}
