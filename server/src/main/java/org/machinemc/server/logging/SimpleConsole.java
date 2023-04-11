package org.machinemc.server.logging;

import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.server.Machine;

import java.io.*;
import java.util.logging.Level;

/**
 * Represents a basic console implementation with colors support intended to be compatible with all consoles.
 */
public class SimpleConsole extends BaseConsole {

    private final PrintStream out;
    private final InputStream in;

    public SimpleConsole(Machine server, boolean colors, PrintStream out, InputStream in) {
        super(server, colors);
        this.out = out;
        this.in = in;
    }

    @Override
    public void start() {
        super.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Scheduler.task((i, session) -> {
            while(isRunning()) {
                try {
                    final String command = reader.readLine();
                    execute(command);
                } catch (Exception ignored) { }
            }
            return null;
        }).async().delay(1).run(getServer().getScheduler());
    }

    @Override
    public void log(Level level, String... messages) {
        log(out::println, level, messages);
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

}
