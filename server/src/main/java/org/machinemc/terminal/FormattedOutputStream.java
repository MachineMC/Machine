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
package org.machinemc.terminal;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * A custom {@link FilterOutputStream} that formats output lines with a prefix and logs them using a given logger.
 * <p>
 * It automatically detects and writes line separators based on the system's default line separator.
 */
public class FormattedOutputStream extends FilterOutputStream {

    private static final byte[] LINE_SEPARATOR = System.lineSeparator().getBytes();

    private final Consumer<String> logger;
    private final String linePrefix;

    private EolTrackerByteArrayOutputStream buf = new EolTrackerByteArrayOutputStream();

    public FormattedOutputStream(final Consumer<String> logger, final OutputStream os, final @Nullable String linePrefix) {
        super(Preconditions.checkNotNull(os, "Wrapped output stream can not be null"));
        this.logger = Preconditions.checkNotNull(logger, "Logger can not be null");
        this.linePrefix = linePrefix != null ? linePrefix : "";
    }

    @Override
    public void write(final int b) {
        buf.write(b);
        if (buf.isLineComplete()) {
            final String line = new String(buf.toByteArray(), 0, buf.size() - LINE_SEPARATOR.length);
            logger.accept(linePrefix + line);
            buf = new EolTrackerByteArrayOutputStream();
        }
    }

    /**
     * Output stream that provides a method to check if the current buffer content forms a complete line.
     */
    private static final class EolTrackerByteArrayOutputStream extends ByteArrayOutputStream {

        /**
         * Checks if the current buffer content represents a complete line.
         *
         * @return if the buffer content forms a complete line
         */
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
