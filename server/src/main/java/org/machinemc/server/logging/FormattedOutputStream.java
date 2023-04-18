package org.machinemc.server.logging;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class FormattedOutputStream extends FilterOutputStream {

    private static final byte[] LINE_SEPARATOR = System.lineSeparator().getBytes();

    private final ServerConsole console;
    private final Level level;
    private final String linePrefix;
    private EolTrackerByteArrayOutputStream buf = new EolTrackerByteArrayOutputStream();

    public FormattedOutputStream(final ServerConsole console, final Level level, final String linePrefix) {
        super(console.terminal.output());
        this.level = level;
        this.console = console;
        this.linePrefix = linePrefix;
    }

    @Override
    public void write(final int b) throws IOException {
        buf.write(b);
        if (buf.isLineComplete()) {
            final String line = new String(buf.toByteArray(), 0, buf.size() - LINE_SEPARATOR.length);
            console.log(level, linePrefix + line);
            buf = new EolTrackerByteArrayOutputStream();
        }
    }

    private static class EolTrackerByteArrayOutputStream extends ByteArrayOutputStream {

        public boolean isLineComplete() {
            if (count < LINE_SEPARATOR.length)
                return false;

            for (int i = 0; i < LINE_SEPARATOR.length; i++) {
                if (buf[count - LINE_SEPARATOR.length + i] != LINE_SEPARATOR[i])
                    return false;
            }
            return true;
        }

    }
}
