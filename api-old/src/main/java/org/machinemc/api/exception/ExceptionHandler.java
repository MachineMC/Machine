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
