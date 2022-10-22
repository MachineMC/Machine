package me.pesekjak.machine.server.schedule;

@FunctionalInterface
public interface TaskRunnable<R> {

    /**
     * Runs the task.
     * @param input input returned from the last task
     * @param session current task session
     * @return input for the next task
     */
    R run(Object input, TaskSession session);

}
