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

import java.util.Optional;
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
     * Returns ticker for current thread if such a ticker
     * exists.
     *
     * @return ticker for current thread
     */
    static Optional<Ticker> current() {
        final Thread current = Thread.currentThread();
        if (!(current instanceof TickThread tickThread)) return Optional.empty();
        return Optional.of(tickThread.getTicker());
    }

    /**
     * Returns whether the ticker accepts new tasks;
     * meaning is ticking or may be ticking in the future.
     * <p>
     * If the ticker is frozen but not closed, this will always return {@code true}.
     * If the ticker is closed, it means it can not tick again and this
     * will always return {@code false}.
     *
     * @return whether the ticker is running
     */
    boolean acceptsTasks();

    /**
     * Returns whether the ticker is currently frozen.
     * <p>
     * In frozen state it is possible to call {@link #step(int)} to
     * advances the processing by the specified number of ticks.
     *
     * @return whether the ticker is frozen
     */
    boolean isFrozen();

    /**
     * Stops the ticker from ticking.
     * <p>
     * Does nothing if the ticker is already frozen.
     * <p>
     * This does not happen instantly. The current tick is finished and
     * then the ticker is frozen.
     * <p>
     * Freezing the ticker has no effect on {@link #getTickRate()}.
     *
     * @return future of when the ticker is frozen, if the ticker is closed during the
     * freeze request, the future is cancelled
     * @see #isFrozen()
     */
    CompletableFuture<Void> freeze();

    /**
     * Starts ticking again if the ticker is frozen.
     * <p>
     * Does nothing if the ticker is running.
     *
     * @return whether the ticker has been unfrozen
     * @see #isFrozen()
     */
    boolean unfreeze();

    /**
     * Advances the processing by one tick.
     * <p>
     * Stepping ticks forward has no effect on {@link #getTickRate()}.
     *
     * @return future when the ticks are processed
     */
    default CompletableFuture<Void> step() {
        return step(1);
    }

    /**
     * Advances the processing by the specified number of ticks.
     * <p>
     * Stepping ticks forward has no effect on {@link #getTickRate()}.
     *
     * @param ticks number of ticks to process
     * @return future when the ticks are processed
     */
    CompletableFuture<Void> step(int ticks);

    /**
     * Returns tick rate of the ticker.
     * <p>
     * This is not the value set by {@link #setTargetTickRate(float)} but
     * the real tick rate of the ticker.
     *
     * @return server tick rate or {@code 0} if the information is not available
     */
    float getTickRate();

    /**
     * Returns the target tick rate of the ticker under
     * perfect conditions.
     *
     * @return target ticker
     */
    float getTargetTickRate();

    /**
     * Changes the target tick rate of the ticker.
     * <p>
     * This is not the value received by {@link #getTickRate()} but
     * the target tick rate of the ticker under perfect conditions.
     *
     * @param tickRate target tick rate
     */
    void setTargetTickRate(float tickRate);

    /**
     * Returns the average tick duration of this ticker
     * in milliseconds.
     *
     * @return average tick duration or {@code 0} if the information is not available
     */
    default long getAverageTickDuration() {
        return (long) (getTargetTickRate() / getTickRate() * getTargetTickDuration());
    }

    /**
     * Returns the target tick duration under perfect
     * conditions.
     *
     * @return target tick duration
     */
    default long getTargetTickDuration() {
        return (long) (1000 / getTargetTickRate());
    }

    /**
     * Runs given task the next tick.
     *
     * @param supplier task to run
     * @return future
     * @param <T> return type
     */
    default <T> CompletableFuture<T> runNextTick(Supplier<T> supplier) {
        return runAfter(supplier, 0);
    }

    /**
     * Runs given task the next tick.
     *
     * @param supplier task to run
     * @param callback callback
     * @param <T> return type
     */
    default <T> void runNextTick(Supplier<T> supplier, @Nullable FutureCallback<T> callback) {
        runAfter(supplier, 0, callback);
    }

    /**
     * Runs given task the next tick.
     *
     * @param runnable task to run
     * @return future
     */
    default CompletableFuture<Void> runNextTick(Runnable runnable) {
        return runNextTick(() -> { runnable.run(); return null; });
    }

    /**
     * Runs given task the next tick.
     *
     * @param runnable task to run
     * @param callback callback
     */
    default void runNextTick(Runnable runnable, @Nullable FutureCallback<Void> callback) {
        runNextTick(() -> { runnable.run(); return null; }, callback);
    }

    /**
     * Runs given task after given number of ticks.
     *
     * @param supplier task to run
     * @param ticks delay in ticks
     * @return future
     * @param <T> return type
     */
    default <T> CompletableFuture<T> runAfter(Supplier<T> supplier, long ticks) {
        return runRepeatedly(supplier, ticks, -1);
    }

    /**
     * Runs given task after given number of ticks.
     *
     * @param supplier task to run
     * @param ticks delay in ticks
     * @param callback callback
     * @param <T> return type
     */
    default <T> void runAfter(Supplier<T> supplier, long ticks, @Nullable FutureCallback<T> callback) {
        runRepeatedly(supplier, ticks, -1, callback);
    }

    /**
     * Runs given task after given number of ticks.
     *
     * @param runnable task to run
     * @param ticks delay in ticks
     * @return future
     */
    default CompletableFuture<Void> runAfter(Runnable runnable, long ticks) {
        return runRepeatedly(() -> { runnable.run(); return null; }, ticks, -1);
    }

    /**
     * Runs given task after given number of ticks with given period.
     *
     * @param runnable task to run
     * @param ticks delay in ticks
     * @param callback callback
     */
    default void runAfter(Runnable runnable, long ticks, @Nullable FutureCallback<Void> callback) {
        runRepeatedly(() -> { runnable.run(); return null; }, ticks, -1, callback);
    }


    /**
     * Runs given task after given number of ticks with given period.
     *
     * @param supplier task to run
     * @param ticks delay in ticks
     * @param period period, {@code -1} to run the task only once and not repeat it
     * @return future
     * @param <T> return type
     */
    <T> CompletableFuture<T> runRepeatedly(Supplier<T> supplier, long ticks, long period);

    /**
     * Runs given task after given number of ticks with given period.
     *
     * @param supplier task to run
     * @param ticks delay in ticks
     * @param period period, {@code -1} to run the task only once and not repeat it
     * @param callback callback
     * @param <T> return type
     */
    <T> void runRepeatedly(Supplier<T> supplier, long ticks, long period, @Nullable FutureCallback<T> callback);

    /**
     * Runs given task after given number of ticks with given period.
     *
     * @param runnable task to run
     * @param ticks delay in ticks
     * @param period period, {@code -1} to run the task only once and not repeat it
     * @return future
     */
    default CompletableFuture<Void> runRepeatedly(Runnable runnable, long ticks, long period) {
        return runRepeatedly(() -> { runnable.run(); return null; }, ticks, period);
    }

    /**
     * Runs given task after given number of ticks with given period.
     *
     * @param runnable task to run
     * @param ticks delay in ticks
     * @param period period, {@code -1} to run the task only once and not repeat it
     * @param callback callback
     */
    default void runRepeatedly(Runnable runnable, long ticks, long period, @Nullable FutureCallback<Void> callback) {
        runRepeatedly(() -> { runnable.run(); return null; }, ticks, period, callback);
    }

    /**
     * Returns whether the current thread is the tick thread.
     *
     * @return whether the current thread is the tick thread
     */
    default boolean isTickThread() {
        return isTickThread(Thread.currentThread());
    }

    /**
     * Returns whether the given thread is the tick thread.
     *
     * @param thread thread
     * @return whether the given thread is the tick thread
     */
    boolean isTickThread(Thread thread);

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


    /**
     * Shutdowns the ticker.
     * <p>
     * After that ticker can not accept any new tasks or continue ticking.
     * <p>
     * Before the ticker is shutdown, it will complete the current tick,
     * if the ticker is frozen, it can not be closed safely.
     * <p>
     * To shut down frozen ticker, it is necessary to call {@link ScheduledExecutorService#shutdownNow()}
     * of service return by {@link #getTickScheduledExecutor()}. This way the current tick will not be
     * finished and list of unfinished tasks will be returned.
     */
    void close();

}
