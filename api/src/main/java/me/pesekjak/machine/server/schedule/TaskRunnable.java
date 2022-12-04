package me.pesekjak.machine.server.schedule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Task run by the schedulers.
 * @param <R> output
 */
@FunctionalInterface
public interface TaskRunnable<R> {

    /**
     * Runs the task.
     * @param input input returned from the last task
     * @param session current task session
     * @return input for the next task
     */
    R run(@Nullable Object input, @NotNull TaskSession session);

}
