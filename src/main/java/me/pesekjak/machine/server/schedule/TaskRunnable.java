package me.pesekjak.machine.server.schedule;

@FunctionalInterface
public interface TaskRunnable<R> {

    R run(Object input, TaskSession session);

}
