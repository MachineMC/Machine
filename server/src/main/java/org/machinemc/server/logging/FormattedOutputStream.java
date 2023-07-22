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

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class FormattedOutputStream extends FilterOutputStream {

    private static final byte[] LINE_SEPARATOR = System.lineSeparator().getBytes();

    private final Logger logger;
    private final String linePrefix;
    private EolTrackerByteArrayOutputStream buf = new EolTrackerByteArrayOutputStream();

    public FormattedOutputStream(final Logger logger,
                                 final OutputStream out,
                                 final @Nullable String linePrefix) {
        super(Objects.requireNonNull(out));
        this.logger = Objects.requireNonNull(logger, "Logger can not be null");
        this.linePrefix = linePrefix != null ? linePrefix : "";
    }

    @Override
    public void write(final int b) throws IOException {
        buf.write(b);
        if (buf.isLineComplete()) {
            final String line = new String(buf.toByteArray(), 0, buf.size() - LINE_SEPARATOR.length);
            logger.log(linePrefix + line);
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
