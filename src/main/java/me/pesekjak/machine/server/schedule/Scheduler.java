package me.pesekjak.machine.server.schedule;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

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

    public static Builder task(TaskRunnable<?> task) {
        return new Builder(new TaskSession(task));
    }

    public static class Builder {

        private final TaskSession startPoint;
        private TaskSession current;
        private final List<TaskSession> tasks = new ArrayList<>();

        public Builder(TaskSession startPoint) {
            this.startPoint = startPoint;
            current = startPoint;
        }

        public Builder sync() {
            return execution(TaskSession.Execution.SYNC);
        }

        public Builder async() {
            return execution(TaskSession.Execution.ASYNC);
        }

        private Builder execution(TaskSession.Execution execution) {
            current.execution = execution;
            return this;
        }

        public Builder repeat(boolean repeat) {
            current.repeating = repeat;
            return this;
        }

        public Builder delay(long delay) {
            current.delay = delay;
            return this;
        }

        public Builder period(long period) {
            current.period = period;
            return this;
        }

        public Builder unit(TimeUnit unit) {
            current.unit = unit;
            return this;
        }

        public Builder then(TaskRunnable<?> next) {
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
