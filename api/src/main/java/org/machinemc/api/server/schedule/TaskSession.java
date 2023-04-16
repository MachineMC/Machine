package org.machinemc.api.server.schedule;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Chained task that can be run on a
 * scheduler.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TaskSession {

    private Scheduler scheduler;
    private boolean running = false;

    private final TaskRunnable<?> runnable;
    protected Execution execution = Execution.SYNC;
    protected boolean repeating = false;
    protected long delay = 0;
    protected long period = 1000;
    protected TimeUnit unit = TimeUnit.MILLISECONDS;

    protected @Nullable TaskSession previous;
    protected @Nullable TaskSession future;

    protected @Nullable Object input;
    protected final AtomicReference<Object> output = new AtomicReference<>();

    protected @Nullable TaskRunnable<?> wrapped;

    private ScheduledFuture<?> asyncScheduledFuture;

    /**
     * Runs the task.
     * @param scheduler scheduler to run the task on
     */
    protected void run(final Scheduler scheduler) {
        if (running)
            throw new IllegalStateException("You can't run the same task twice");
        running = true;
        this.scheduler = scheduler;
        scheduler.sessions.add(this);
        this.input = previous != null ? previous.output.get() : null;

        if (execution == Execution.SYNC) {
            if (!repeating) {
                wrapped = (i, session) -> {
                    output.set(runnable.run(i, session));
                    runFuture();
                    return null;
                };
                scheduler.getThreadPoolExecutor().schedule(() -> {
                    scheduler.getSyncQueue().add(this);
                }, delay, unit);
            } else {
                wrapped = (i, session) -> {
                    output.set(runnable.run(i, session));
                    return null;
                };
                scheduler.getThreadPoolExecutor().scheduleAtFixedRate(
                        () -> scheduler.getSyncQueue().add(this),
                        delay, period, unit
                );
            }
        } else if (execution == Execution.ASYNC) {
            if (!repeating) {
                wrapped = (i, session) -> {
                    asyncScheduledFuture = scheduler.getThreadPoolExecutor().schedule(() -> {
                        output.set(runnable.run(i, session));
                        runFuture();
                    }, delay, unit);
                    return null;
                };
            } else {
                wrapped = (i, session) -> {
                    asyncScheduledFuture = scheduler.getThreadPoolExecutor().scheduleAtFixedRate(
                            () -> output.set(runnable.run(i, session)),
                            delay, period, unit
                    );
                    return null;
                };
            }
            wrapped.run(input, this);
        }
    }

    /**
     * Runs the next task in the order.
     */
    private void runFuture() {
        scheduler.sessions.remove(this);
        if (future == null) return;
        if (!scheduler.isRunning()) return;
        future.run(scheduler);
    }

    /**
     * Stops the task from repeating, stops the running code and
     * then runs the next task in the order.
     */
    public void stop() {
        stop(false, true);
    }

    /**
     * Stops the task from repeating and the running code.
     * @param interrupt if the thread executing this task should be interrupted
     * @param next if the next task should be run
     */
    public void stop(final boolean interrupt, final boolean next) {
        if (!running)
            throw new IllegalStateException("You can't stop not running task");
        if (asyncScheduledFuture != null) {
            asyncScheduledFuture.cancel(interrupt);
            if (next) runFuture();
        }
    }

    /**
     * Terminates the task.
     */
    protected void terminate() {
        if (asyncScheduledFuture != null)
            asyncScheduledFuture.cancel(true);
    }

    /**
     * Represents the execution of the task.
     */
    public enum Execution {
        SYNC,
        ASYNC
    }

}
