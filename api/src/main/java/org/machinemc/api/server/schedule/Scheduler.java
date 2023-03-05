package org.machinemc.api.server.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

/**
 * Scheduler, can schedule tasks on a thread.
 * <p>
 * To block the current thread {@link Scheduler#run()} is used,
 * to run code on that thread, sync tasks have to be run on that
 * scheduler instance from different threads. To unblock the
 * thread {@link Scheduler#shutdown()} should be used, scheduler can
 * be then run again on the same or a different thread.
 * @see Scheduler#task(TaskRunnable)
 * @see TaskBuilder#run(Scheduler)
 */
@BlockingExecutor
public class Scheduler {

    @Getter(AccessLevel.PROTECTED)
    private final @NotNull BlockingQueue<TaskSession> syncQueue;
    @Getter(AccessLevel.PROTECTED)
    private final @NotNull ScheduledExecutorService threadPoolExecutor;

    protected final HashSet<TaskSession> sessions = new HashSet<>();

    @Getter
    private boolean running = false;

    /**
     * Creates scheduler for the current thread.
     * @param threadPoolSize thread pool size for the executor
     */
    public Scheduler(int threadPoolSize) {
        syncQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = Executors.newScheduledThreadPool(threadPoolSize, Executors.defaultThreadFactory());
    }

    /**
     * Runs the scheduler on the current thread and blocks it until
     * the scheduler is shutdown, to run tasks on the blocked thread
     * {@link Scheduler#task(TaskRunnable)} can be used.
     * @throws InterruptedException if interrupted while running
     */
    @Blocking
    public void run() throws InterruptedException {
        running = true;
        sessions.clear();
        while (!Thread.interrupted() && running) {
            TaskSession next = syncQueue.take();
            if(running && next.wrapped != null)
                next.wrapped.run(next.input, next);
        }
    }

    /**
     * Shutdown the scheduler and unblocks the thread it was originally run
     * from, to start the scheduler again {@link Scheduler#run()} can be used.
     * @throws InterruptedException if interrupted while shutting down
     */
    @NonBlocking
    public void shutdown() throws InterruptedException {
        running = false;
        syncQueue.clear();
        syncQueue.put(new TaskSession((input, session) -> null)); // unblocking
        for(TaskSession session : sessions)
            session.terminate();
    }

    @Contract("_ -> new")
    public static @NotNull TaskBuilder task(@NotNull TaskRunnable<?> task) {
        return new TaskBuilder(new TaskSession(task));
    }

    /**
     * Builder for tasks.
     */
    public static class TaskBuilder {

        private final @NotNull TaskSession startPoint;
        private TaskSession current;
        private final List<TaskSession> tasks = new ArrayList<>();

        protected TaskBuilder(@NotNull TaskSession startPoint) {
            this.startPoint = startPoint;
            current = startPoint;
        }

        /**
         * Makes the task run synchronized on the scheduler's main thread.
         */
        @Contract("-> this")
        public @NotNull TaskBuilder sync() {
            return execution(TaskSession.Execution.SYNC);
        }

        /**
         * Makes the task run asynchronously.
         */
        @Contract("-> this")
        public @NotNull TaskBuilder async() {
            return execution(TaskSession.Execution.ASYNC);
        }

        @Contract("_ -> this")
        private @NotNull TaskBuilder execution(@NotNull TaskSession.Execution execution) {
            current.execution = execution;
            return this;
        }

        /**
         * @param repeat true if the task should repeat itself until it's cancel from inside
         */
        @Contract("_ -> this")
        public @NotNull TaskBuilder repeat(boolean repeat) {
            current.repeating = repeat;
            return this;
        }

        /**
         * @param delay delay the task should have before the first execution
         */
        @Contract("_ -> this")
        public @NotNull TaskBuilder delay(@Range(from = 0, to = Long.MAX_VALUE) long delay) {
            current.delay = delay;
            return this;
        }

        /**
         * @param period how big should be the delay between next task repetition
         */
        @Contract("_ -> this")
        public @NotNull TaskBuilder period(@Range(from = 0, to = Long.MAX_VALUE) long period) {
            current.period = period;
            return this;
        }

        /**
         * @param unit time unit of the delays
         */
        @Contract("_ -> this")
        public @NotNull TaskBuilder unit(TimeUnit unit) {
            current.unit = unit;
            return this;
        }

        /**
         * Adds next task to the chain.
         * @param next next task
         */
        @Contract("_ -> this")
        public @NotNull TaskBuilder then(@NotNull TaskRunnable<?> next) {
            TaskSession nextSession = new TaskSession(next);
            nextSession.previous = current;
            current.future = nextSession;
            current = nextSession;
            return this;
        }

        /**
         * Runs the task on given shceduler.
         * @param scheduler scheduler to run the task on
         */
        public void run(@NotNull Scheduler scheduler) {
            startPoint.run(scheduler);
        }

    }

}
