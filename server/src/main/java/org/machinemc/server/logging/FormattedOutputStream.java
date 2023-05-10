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
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.logging;

import org.machinemc.api.logging.Console;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class FormattedOutputStream extends FilterOutputStream {

    private static final byte[] LINE_SEPARATOR = System.lineSeparator().getBytes();

    private final Console console;
    private final Level level;
    private final String linePrefix;
    private EolTrackerByteArrayOutputStream buf = new EolTrackerByteArrayOutputStream();

    public FormattedOutputStream(Console console, Level level, String linePrefix) {
        super(console.getOutputStream());
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
