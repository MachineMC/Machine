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

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import lombok.Getter;
import lombok.Locked;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Task scheduled by {@link TickerImpl}.
 *
 * @param <T> return type
 */
class TickingTask<T> implements ScheduledFuture<T> {

    /**
     * Callable the task runs.
     */
    @Getter
    private final Callable<T> callable;

    /**
     * Initial delay of the execution in ticks.
     * <p>
     * {@code 0} if there is none.
     */
    @Getter
    private final long delay;

    /**
     * Period between executions if the task
     * is scheduled at fixed rate or delay in ticks.
     * <p>
     * {@code -1} if there is none.
     */
    @Getter
    private final long period;

    /**
     * Future callback called on task completion.
     */
    private final @Nullable FutureCallback<T> callback;

    /**
     * Number of remaining ticks until the next
     * execution.
     */
    @Getter
    private volatile long remainingTicks;

    //
    // Tracks the state of the task.
    //
    @Setter
    private volatile boolean running = false;
    private volatile boolean cancelled = false;
    private volatile boolean done = false;

    /**
     * Result of the ticking task.
     */
    private @Nullable T result;

    /**
     * If the task threw an exception.
     */
    private @Nullable Exception exception;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition(); // condition for finishing the task

    TickingTask(final Callable<T> callable, final long delay, final long period, final @Nullable FutureCallback<T> callback) {
        this.callable = Preconditions.checkNotNull(callable, "Callable can not be null");
        this.delay = Math.max(0, delay);
        this.period = Math.max(-1, period);
        this.callback = callback;

        remainingTicks = this.delay;
    }

    TickingTask(final Runnable command, final long delay) {
        this(() -> {
            command.run();
            return null;
            }, delay);
    }

    TickingTask(final Callable<T> callable, final long delay) {
        this(callable, delay, -1, null);
    }

    TickingTask(final Runnable command, final long delay, final long period) {
        this(() -> {
            command.run();
            return null;
            }, delay, period, null);
    }

    /**
     * Resets the remaining ticks to the task period.
     */
    final void resetRemainingTicks() {
        Preconditions.checkState(period != -1, "This task can not reset");
        remainingTicks = period;
    }

    @Locked
    @SuppressWarnings("NonAtomicOperationOnVolatileField") // the method is locked using lombok
    final void tick() {
        Preconditions.checkState(remainingTicks > 0, "Task is ready to execute");
        remainingTicks--;
    }

    /**
     * Finishes the task.
     *
     * @param result task result
     * @param exception if the task threw an exception.
     */
    final void finish(final @Nullable T result, final @Nullable Exception exception) {
        Preconditions.checkState(!isDone(), "This task has already been finished");
        lock.lock();
        try {
            done = true;

            this.result = result;
            this.exception = exception;

            if (callback != null) {
                if (exception == null) callback.onSuccess(result);
                else callback.onFailure(exception);
            }

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getDelay(final @NotNull TimeUnit unit) {
        return Tick.of(remainingTicks).get(unit.toChronoUnit());
    }

    @Override
    public int compareTo(final @NotNull Delayed o) {
        return Long.compare(o.getDelay(TimeUnit.MILLISECONDS), getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        if (running || cancelled || done) return false;
        cancelled = true;
        finish(null, null);
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return cancelled || done;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            condition.await();
            handleResult(false); // this can never time out
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    @Override
    public T get(final long timeout, final @NotNull TimeUnit unit) throws ExecutionException, TimeoutException {
        try {
            handleResult(!condition.await(timeout, unit));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    /**
     * Throws an according exception if the task
     * has been finished exceptionally.
     *
     * @param timeout whether the task wait timed out
     */
    @SneakyThrows
    private void handleResult(final boolean timeout) {
        if (cancelled) throw new CancellationException();
        if (timeout) throw new TimeoutException();
        if (exception != null) throw new ExecutionException(exception);
    }

}
