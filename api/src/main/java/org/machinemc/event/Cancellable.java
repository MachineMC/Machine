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
package org.machinemc.event;

/**
 * An interface that can be implemented by {@link Event}s to mark the event as cancellable
 */
public interface Cancellable {

    /**
     * Checks if the event is cancelled.
     *
     * @return true if the event is cancelled, false otherwise
     */
    boolean cancelled();

    /**
     * Sets the cancellation state of the event.
     *
     * @param cancelled the new cancellation state
     */
    void cancelled(boolean cancelled);

    /**
     * Sets the cancellation state of the event to true.
     */
    default void cancel() {
        cancelled(true);
    }

}
