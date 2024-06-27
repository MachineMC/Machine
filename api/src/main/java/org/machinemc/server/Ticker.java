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
package org.machinemc.server;

import com.google.common.util.concurrent.FutureCallback;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * The Ticker is a core component of the server responsible for managing time within the game.
 * <p>
 * It ensures that the game world is updated at regular intervals, known as "ticks".
 * A server tick is a single cycle of the game loop where various game mechanics are updated,
 * including entity movement, block changes, and scheduled events.
 * <p>
 * This always happens on the main server thread (tick thread).
 * <p>
 * The game runs at a fixed tick rate of 20 ticks per second (TPS),
 * meaning each tick occurs every 50 milliseconds.
 * Maintaining a consistent TPS is crucial for smooth gameplay.
 */
public interface Ticker extends AutoCloseable {

    /**
     * Returns whether the ticker is ticking (is active).
     *
     * @return whether the ticker is active
     */
    boolean isRunning();

    /**
     * Returns tick rate of the server.
     *
     * @return server tick rate
     */
    @Range(from = 0, to = 20) float getTickRate();

    /**
     * Runs given task the next game tick.
     *
     * @param supplier task to run
     * @return future
     * @param <T> return type
     */
    <T> CompletableFuture<T> runNextTick(Supplier<T> supplier);

    /**
     * Runs given task the next game tick.
     *
     * @param supplier task to run
     * @param callback callback
     * @param <T> return type
     */
    <T> void runNextTick(Supplier<T> supplier, @Nullable FutureCallback<T> callback);

    /**
     * Runs given task the next game tick.
     *
     * @param runnable task to run
     * @return future
     */
    default CompletableFuture<Void> runNextTick(Runnable runnable) {
        return runNextTick(() -> { runnable.run(); return null; });
    }

    /**
     * Runs given task the next game tick.
     *
     * @param runnable task to run
     * @param callback callback
     */
    default void runNextTick(Runnable runnable, @Nullable FutureCallback<Void> callback) {
        runNextTick(() -> { runnable.run(); return null; }, callback);
    }

    /**
     * Returns whether the current thread is the tick thread.
     *
     * @return whether the current thread is the tick thread
     */
    default boolean isTickThread() {
        return isTickTread(Thread.currentThread());
    }

    /**
     * Returns whether the given thread is the tick thread.
     *
     * @param thread thread
     * @return whether the given thread is the tick thread
     */
    boolean isTickTread(Thread thread);

    /**
     * Returns an executor that will run tasks on the next server tick.
     *
     * @return executor using the tick thread
     */
    default Executor getTickThreadExecutor() {
        return this::runNextTick;
    }

    /**
     * Returns a scheduled executor that will run tasks on the main thread,
     * using {@link Tick} for conversion for delays and periods.
     * <p>
     * Meaning if task is scheduled to run with delay of 1 second, it
     * will run 20 ticks later which can be longer if the server does
     * not operate smoothly.
     * <p>
     * Due to nature of ticks, it makes no difference whether the tasks
     * scheduled by this service run at fixed rate or with fixed delay.
     * The period of the scheduled task is recalculated as number of ticks
     * using {@link Tick} for conversion. For tick to complete,
     * all pending tasks need to be executed. This makes the scheduling
     * at fixed rate impossible because the next tick is always delayed
     * by the task execution.
     *
     * @return scheduled executor service using the tick thread
     */
    ScheduledExecutorService getTickScheduledExecutor();

}
