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
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.utils.FunctionalFutureCallback;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Implementation of {@link Ticker}.
 */
public class TickerImpl implements Ticker, ScheduledExecutorService {

    private Thread tickThread;

    private volatile boolean frozen = false; // if the ticker is frozen right now

    // future used by the #freeze method, if present, indicates the
    // freeze has been requested and the ticker should freeze before next tick
    private @Nullable CompletableFuture<Void> freezeFuture;

    private final ReentrantLock freezeLock = new ReentrantLock();
    private final Condition freezeCondition = freezeLock.newCondition(); // stops the tick thread if frozen

    // ticks to step forward while being frozen
    private final AtomicLong steppingTicks = new AtomicLong(0);

    /**
     * Target tick rate.
     */
    @Getter
    private volatile float targetTickRate;

    /**
     * Active tasks left to execute.
     */
    private final Set<TickingTask<?>> tasks = Collections.synchronizedSet(new LinkedHashSet<>());

    /**
     * Tasks to add on the next tick.
     */
    private final Set<TickingTask<?>> tasksToAdd = Collections.synchronizedSet(new LinkedHashSet<>());

    /**
     * Tasks to remove before the next tick.
     */
    private final Set<TickingTask<?>> tasksToRemove = Collections.synchronizedSet(new LinkedHashSet<>());

    private volatile boolean shuttingDown = false;
    private volatile boolean shuttingDownNow = false;

    private final CompletableFuture<Void> terminateFuture = new CompletableFuture<>();

    /**
     * List containing the durations of last 20 server ticks in millis.
     */
    private final LongList lastTicks = LongLists.synchronize(new LongArrayList());

    /**
     * Creates and starts a new server ticker.
     *
     * @param tickThread thread builder used for creation of the tick thread
     * @param targetTickRate target tick rate of the ticker
     */
    public TickerImpl(final Thread.Builder tickThread, final float targetTickRate) {
        setTargetTickRate(targetTickRate);
        Preconditions.checkNotNull(tickThread, "Tick thread builder can not be null").start(this::run);
    }

    /**
     * Starts the ticker.
     */
    private void run() {
        Preconditions.checkState(tickThread == null, "Ticker is already running");
        tickThread = Thread.currentThread();
        startTicking();
    }

    /**
     * Executes the next tick.
     */
    @SneakyThrows
    private void startTicking() {
        Preconditions.checkState(isTickThread(), "Ticking on not a tick thread");
        while (true) {
            final Instant tickStart = Instant.now();

            for (final TickingTask<?> task : tasks) {
                if (shuttingDownNow) break;
                if (handleTask(task)) tasksToRemove.add(task);
            }

            if (shuttingDown) {
                terminateFuture.complete(null);
                return;
            }

            tasks.removeAll(tasksToRemove);
            tasksToRemove.clear();

            // normal tick
            if (!frozen && steppingTicks.get() == 0) {
                final long took = ChronoUnit.MILLIS.between(tickStart, Instant.now());
                final long target = (long) (1000 / targetTickRate);

                if (took < target) {
                    //noinspection BusyWait
                    Thread.sleep(target - took);
                    lastTicks.addFirst(target);
                } else {
                    lastTicks.addFirst(took);
                }

                while (lastTicks.size() > 20)
                    lastTicks.removeLast();
            }

            // last stepping tick while frozen
            if (frozen && steppingTicks.get() == 1) {
                steppingTicks.decrementAndGet();
                freezeLock.lock();
                try {
                    freezeCondition.await();
                } finally {
                    freezeLock.unlock();
                }
            }

            // freezing the ticker
            if (freezeFuture != null && steppingTicks.get() == 0) {
                freezeLock.lock();
                try {
                    frozen = true;
                    freezeFuture.complete(null);
                    freezeFuture = null;
                    freezeCondition.await();
                } finally {
                    freezeLock.unlock();
                }
            }

            tasks.addAll(tasksToAdd);
            tasksToAdd.clear();
        }
    }

    /**
     * Handles the next task to execute.
     * <p>
     * This can either execute the task if it is ready for execution or
     * delay it for later.
     * <p>
     * This also takes care of the rescheduling the task if needed.
     *
     * @param task task to handle
     * @return whether the task should be removed
     */
    @SuppressWarnings("unchecked")
    private boolean handleTask(final TickingTask<?> task) {
        if (task.isCancelled()) {
            return true;
        }

        if (shuttingDownNow) {
            return false;
        }

        if (task.getRemainingTicks() != 0) {
            task.tick();
            return false;
        }

        if (task.getPeriod() == -1) {
            executeTask((TickingTask<Object>) task, true);
            return true;
        } else {
            task.resetRemainingTicks();
            executeTask((TickingTask<Object>) task, false);
            return task.isDone();
        }
    }

    /**
     * Executes given task.
     * <p>
     * The task is completed if exception is thrown during the execution or is marked
     * as {@code shouldFinish}.
     *
     * @param task task to execute
     * @param shouldFinish whether the task is expected to finish or will be rescheduled later
     */
    private void executeTask(final TickingTask<Object> task, final boolean shouldFinish) {
        task.setRunning(true);
        try {
            final Object result = task.getCallable().call();
            if (shouldFinish) task.finish(result, null);
        } catch (Exception exception) {
            task.finish(null, exception);
        }
        task.setRunning(false);
    }

    /**
     * Adds task to the tasks to run.
     *
     * @param task task to add
     * @return task
     * @param <T> return type
     */
    @Contract("_ -> param1")
    private <T> TickingTask<T> addTask(final TickingTask<T> task) {
        if (shuttingDown) return task;
        Preconditions.checkNotNull(task, "Task can not be null");
        Preconditions.checkState(!task.isDone(), "Task has already finished");
        tasksToAdd.add(task);
        return task;
    }

    @Override
    public boolean acceptsTasks() {
        return !shuttingDown;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public CompletableFuture<Void> freeze() {
        if (frozen) return CompletableFuture.completedFuture(null);
        freezeLock.lock();
        try {
            if (freezeFuture == null) freezeFuture = new CompletableFuture<>();
            return freezeFuture;
        } finally {
            freezeLock.unlock();
        }
    }

    @Override
    public boolean unfreeze() {
        if (!frozen) return false;
        frozen = false;
        freezeLock.lock();
        try {
            freezeCondition.signalAll();
            return true;
        } finally {
            freezeLock.unlock();
        }
    }

    @Override
    public CompletableFuture<Void> step(final int ticks) {
        if (ticks <= 0) return CompletableFuture.completedFuture(null);
        steppingTicks.addAndGet(ticks);
        final CompletableFuture<Void> future = runAfter(() -> null, ticks - 1); // 0 means running next tick which is the same as step 1 tick forward
        if (frozen) {
            freezeLock.lock();
            try {
                freezeCondition.signalAll();
            } finally {
                freezeLock.unlock();
            }
        }
        return future;
    }

    @Override
    public float getTickRate() {
        return (float) (1000 / lastTicks.longStream().average().orElse(0));
    }

    @Override
    public void setTargetTickRate(final float tickRate) {
        Preconditions.checkState(tickRate > 0, "Target tick rate has to be more than 0");
        targetTickRate = tickRate;
    }

    @Override
    public <T> CompletableFuture<T> runRepeatedly(final Supplier<T> supplier, final long ticks, final long period) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        final AtomicReference<TickingTask<T>> reference = new AtomicReference<>();
        final TickingTask<T> task = new TickingTask<>(supplier::get, ticks, period, FunctionalFutureCallback.create(
                success -> {
                    if (reference.get().isCancelled()) future.cancel(true);
                    else future.complete(success);
                },
                future::completeExceptionally
        ));
        reference.set(task);
        addTask(task);
        return future;
    }

    @Override
    public <T> void runRepeatedly(final Supplier<T> supplier, final long ticks, final long period, final @Nullable FutureCallback<T> callback) {
        addTask(new TickingTask<>(supplier::get, ticks, period, callback));
    }

    @Override
    public boolean isTickTread(final Thread thread) {
        return tickThread.equals(thread);
    }

    @Override
    public ScheduledExecutorService getTickScheduledExecutor() {
        return this;
    }

    @Override
    public void close() {
        ScheduledExecutorService.super.close();
    }

    @Override
    public @NotNull ScheduledFuture<?> schedule(final @NotNull Runnable command, final long delay, final @NotNull TimeUnit unit) {
        return addTask(new TickingTask<>(command, Tick.of(delay, unit)));
    }

    @Override
    public <V> @NotNull ScheduledFuture<V> schedule(final @NotNull Callable<V> callable, final long delay, final @NotNull TimeUnit unit) {
        return addTask(new TickingTask<>(callable, Tick.of(delay, unit)));
    }

    @Override
    public @NotNull ScheduledFuture<?> scheduleAtFixedRate(final @NotNull Runnable command, final long initialDelay, final long period, final @NotNull TimeUnit unit) {
        return addTask(new TickingTask<>(command, Tick.of(initialDelay, unit), Tick.of(period, unit)));
    }

    @Override
    public @NotNull ScheduledFuture<?> scheduleWithFixedDelay(final @NotNull Runnable command, final long initialDelay, final long delay, final @NotNull TimeUnit unit) {
        return addTask(new TickingTask<>(command, Tick.of(initialDelay, unit), Tick.of(delay, unit)));
    }

    @Override
    public void shutdown() {
        shuttingDown = true;
        if (freezeFuture != null) freezeFuture.cancel(true);
    }

    @Override
    public @NotNull List<Runnable> shutdownNow() {
        shutdown();
        shuttingDownNow = true;
        return tasks.stream()
                .filter(task -> !tasksToRemove.contains(task))
                .map(TickingTask::getCallable)
                .map(callable -> (Runnable) () -> {
                    try {
                        callable.call();
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                })
                .toList();
    }

    @Override
    public boolean isShutdown() {
        return shuttingDown;
    }

    @Override
    public boolean isTerminated() {
        return terminateFuture.isDone();
    }

    @Override
    public boolean awaitTermination(final long timeout, final @NotNull TimeUnit unit) throws InterruptedException {
        if (isTerminated()) return true;
        try {
            terminateFuture.get(timeout, unit);
            return true;
        } catch (ExecutionException exception) {
            throw new AssertionError(null, exception);
        } catch (TimeoutException exception) {
            return false;
        }
    }

    @Override
    public <T> @NotNull Future<T> submit(final @NotNull Callable<T> task) {
        return addTask(new TickingTask<>(task, 0));
    }

    @Override
    public <T> @NotNull Future<T> submit(final @NotNull Runnable task, final T result) {
        return addTask(new TickingTask<>(() -> {
            task.run();
            return result;
        }, 0));
    }

    @Override
    public @NotNull Future<?> submit(final @NotNull Runnable task) {
        return addTask(new TickingTask<>(task, 0));
    }

    @Override
    public <T> @NotNull List<Future<T>> invokeAll(final @NotNull Collection<? extends Callable<T>> tasks) {
        return invokeAll(tasks, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> @NotNull List<Future<T>> invokeAll(final @NotNull Collection<? extends Callable<T>> tasks, final long timeout, final @NotNull TimeUnit unit) {
        return tasks.stream()
                .map(callable -> schedule(callable, timeout, unit))
                .map(future -> (Future<T>) future)
                .toList();
    }

    @Override
    public <T> @NotNull T invokeAny(final @NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        Preconditions.checkNotNull(tasks, "Tasks can not be null");
        Preconditions.checkState(!tasks.isEmpty(), "Tasks collection is empty");

        final CompletionService<T> service = new ExecutorCompletionService<>(this);
        for (final Callable<T> task : tasks) service.submit(task);

        while (true) {
            final Future<T> future = service.poll();
            if (future.state() != Future.State.SUCCESS) continue;
            return future.get();
        }
    }

    @Override
    public <T> T invokeAny(final @NotNull Collection<? extends Callable<T>> tasks, final long timeout, final @NotNull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        Preconditions.checkNotNull(tasks, "Tasks can not be null");

        final CompletionService<T> service = new ExecutorCompletionService<>(this);
        for (final Callable<T> task : tasks) service.submit(task);

        while (true) {
            final Future<T> future = service.poll(timeout, unit);
            if (future == null) throw new TimeoutException();
            if (future.state() != Future.State.SUCCESS) continue;
            return future.get();
        }
    }

    @Override
    public void execute(final @NotNull Runnable command) {
        runNextTick(command);
    }

}
