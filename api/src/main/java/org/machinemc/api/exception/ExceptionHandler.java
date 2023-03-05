package org.machinemc.api.exception;

import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.Nullable;

public interface ExceptionHandler extends ServerProperty {

    /**
     * Handles a throwable thrown by server.
     * @param throwable throwable thrown by server that
     *                  should be handled
     */
    void handle(Throwable throwable);

    /**
     * Handles a throwable thrown by server.
     * @param throwable throwable thrown by server that
     *                  should be handled
     * @param reason reason why the throwable was thrown
     */
    void handle(Throwable throwable, @Nullable String reason);

}
