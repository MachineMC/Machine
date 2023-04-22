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
 * If not, see <https://www.gnu.org/licenses/>.
 */
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
public class Scheduler {

    @Getter(AccessLevel.PROTECTED)
    private final BlockingQueue<TaskSession> syncQueue;
    @Getter
    private final ScheduledExecutorService threadPoolExecutor;

    protected final HashSet<TaskSession> sessions = new HashSet<>();

    @Getter
    private boolean running = false;

    /**
     * Creates scheduler for the current thread.
     * @param threadPoolSize thread pool size for the executor
     */
    public Scheduler(final int threadPoolSize) {
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
            final TaskSession next = syncQueue.take();
            if (running && next.wrapped != null)
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
        for (final TaskSession session : sessions)
            session.terminate();
    }

    /**
     * Creates new task builder for the provided task.
     * @param task task
     * @return task builder
     */
    @Contract("_ -> new")
    public static TaskBuilder task(final TaskRunnable<?> task) {
        return new TaskBuilder(new TaskSession(task));
    }

    /**
     * Builder for tasks.
     */
    public static class TaskBuilder {

        private final TaskSession startPoint;
        private TaskSession current;
        private final List<TaskSession> tasks = new ArrayList<>();

        protected TaskBuilder(final TaskSession startPoint) {
            this.startPoint = startPoint;
            current = startPoint;
        }

        /**
         * Makes the task run synchronized on the scheduler's main thread.
         * @return task builder
         */
        @Contract("-> this")
        public TaskBuilder sync() {
            return execution(TaskSession.Execution.SYNC);
        }

        /**
         * Makes the task run asynchronously.
         * @return task builder
         */
        @Contract("-> this")
        public TaskBuilder async() {
            return execution(TaskSession.Execution.ASYNC);
        }

        /**
         * Changes the execution of the task.
         * @param execution execution
         * @return taks builder
         */
        @Contract("_ -> this")
        private TaskBuilder execution(final TaskSession.Execution execution) {
            current.execution = execution;
            return this;
        }

        /**
         * @param repeat true if the task should repeat itself until it's cancel from inside
         * @return task builder
         */
        @Contract("_ -> this")
        public TaskBuilder repeat(final boolean repeat) {
            current.repeating = repeat;
            return this;
        }

        /**
         * @param delay delay the task should have before the first execution
         * @return task builder
         */
        @Contract("_ -> this")
        public TaskBuilder delay(final long delay) {
            current.delay = delay;
            return this;
        }

        /**
         * @param period how big should be the delay between next task repetition
         * @return task builder
         */
        @Contract("_ -> this")
        public TaskBuilder period(final long period) {
            current.period = period;
            return this;
        }

        /**
         * @param unit time unit of the delays
         * @return task builder
         */
        @Contract("_ -> this")
        public TaskBuilder unit(final TimeUnit unit) {
            current.unit = unit;
            return this;
        }

        /**
         * Adds next task to the chain.
         * @param next next task
         * @return task builder
         */
        @Contract("_ -> this")
        public TaskBuilder then(final TaskRunnable<?> next) {
            final TaskSession nextSession = new TaskSession(next);
            nextSession.previous = current;
            current.future = nextSession;
            current = nextSession;
            return this;
        }

        /**
         * Runs the task on given scheduler.
         * @param scheduler scheduler to run the task on
         */
        public void run(final Scheduler scheduler) {
            startPoint.run(scheduler);
        }

    }

}
