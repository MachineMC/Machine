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
package org.machinemc.api.server.schedule;

import org.jetbrains.annotations.Nullable;

/**
 * Task run by the schedulers.
 * @param <R> output
 */
@FunctionalInterface
public interface TaskRunnable<R> {

    /**
     * Runs the task.
     * @param input input returned from the last task
     * @param session current task session
     * @return input for the next task
     */
    R run(@Nullable Object input, TaskSession session);

}
