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
package org.machinemc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that the returned {@link java.util.concurrent.CompletableFuture}
 * (or similar type of objects such as provided callbacks) of annotated method will be executed on
 * the {@link org.machinemc.server.TickThread} that executed the method if it is available.
 * <p>
 * This is particularly useful in the context of server operations that need to interact with game state or
 * perform actions that require synchronization with the main game loop. If the tick thread is not available,
 * the behavior may depend on the implementation. But mostly it means executing it on an alternative thread.
 * <p>
 * This guarantees that certain tasks are completed in the context of the tick thread, avoiding concurrency issues
 * (because execution never exits the tick thread).
 * As a result of this synchronization with the tick thread, a 1-tick delay is introduced before the CompletableFuture
 * is completed. This delay is necessary to ensure that the task is properly aligned with the tick cycle.
 * <p>
 * This annotation is used solely for documentation purposes.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface TickerAware {
}
