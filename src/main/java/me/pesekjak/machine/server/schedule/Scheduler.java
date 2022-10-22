package me.pesekjak.machine.server.schedule;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

/**
 * Scheduler, can schedule tasks on a thread.
 *
 * To block the current thread {@link Scheduler#run()} is used,
 * to run code on that thread, sync tasks have to be run on that
 * scheduler instance from different threads. To unblock the
 * thread {@link Scheduler#shutdown()} should be used, scheduler can
 * be then run again on the same or a different thread.
 *
 * To run a task, {@link Scheduler#task(TaskRunnable)} and
 * {@link TaskBuilder#run(Scheduler)} is used.
 */
public class Scheduler {

    @Getter(AccessLevel.PROTECTED)
    private final BlockingQueue<TaskSession> syncQueue;
    @Getter(AccessLevel.PROTECTED)
    private final ScheduledExecutorService threadPoolExecutor;

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
     */
    public void run() throws InterruptedException {
        running = true;
        sessions.clear();
        while (!Thread.interrupted() && running) {
            TaskSession next = syncQueue.take();
            if(running)
                next.wrapped.run(next.input, next);
        }
    }

    /**
     * Shutdown the scheduler and unblocks the thread it was originally run
     * from, to start the scheduler again {@link Scheduler#run()} can be used.
     */
    public void shutdown() throws InterruptedException {
        running = false;
        syncQueue.clear();
        syncQueue.put(new TaskSession(null)); // unblocking
        for(TaskSession session : sessions)
            session.terminate();
    }

    public static TaskBuilder task(TaskRunnable<?> task) {
        return new TaskBuilder(new TaskSession(task));
    }

    public static class TaskBuilder {

        private final TaskSession startPoint;
        private TaskSession current;
        private final List<TaskSession> tasks = new ArrayList<>();

        protected TaskBuilder(TaskSession startPoint) {
            this.startPoint = startPoint;
            current = startPoint;
        }

        /**
         * Makes the task run synchronized on the scheduler's main thread.
         */
        public TaskBuilder sync() {
            return execution(TaskSession.Execution.SYNC);
        }

        /**
         * Makes the task run asynchronously.
         */
        public TaskBuilder async() {
            return execution(TaskSession.Execution.ASYNC);
        }

        private TaskBuilder execution(TaskSession.Execution execution) {
            current.execution = execution;
            return this;
        }

        /**
         * @param repeat true if the task should repeat itself until it's cancel from inside
         */
        public TaskBuilder repeat(boolean repeat) {
            current.repeating = repeat;
            return this;
        }

        /**
         * @param delay delay the task should have before the first execution
         */
        public TaskBuilder delay(long delay) {
            current.delay = delay;
            return this;
        }

        /**
         *
         * @param period how big should be the delay between next task repetition
         */
        public TaskBuilder period(long period) {
            current.period = period;
            return this;
        }

        /**
         * @param unit time unit of the delays
         */
        public TaskBuilder unit(TimeUnit unit) {
            current.unit = unit;
            return this;
        }

        /**
         * Adds next task to the chain.
         * @param next next task
         */
        public TaskBuilder then(TaskRunnable<?> next) {
            TaskSession nextSession = new TaskSession(next);
            nextSession.previous = current;
            current.future = nextSession;
            current = nextSession;
            return this;
        }

        public void run(Scheduler scheduler) {
            startPoint.run(scheduler);
        }

    }

}
