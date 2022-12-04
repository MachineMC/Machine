package me.pesekjak.machine.exception;

import me.pesekjak.machine.server.ServerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ExceptionHandler extends ServerProperty {

    /**
     * Handles a throwable thrown by server.
     * @param throwable throwable thrown by server that
     *                  should be handled
     */
    void handle(@NotNull Throwable throwable);

    /**
     * Handles a throwable thrown by server.
     * @param throwable throwable thrown by server that
     *                  should be handled
     * @param reason reason why the throwable was thrown
     */
    void handle(@NotNull Throwable throwable, @Nullable String reason);

}
