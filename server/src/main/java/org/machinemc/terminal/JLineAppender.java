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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import org.jline.reader.LineReader;

/**
 * Logback appender that uses the printAbove method of a JLine {@link LineReader}
 * to print output above a prompt.
 */
public final class JLineAppender extends AppenderBase<ILoggingEvent> {

    private final LineReader lineReader = ServerTerminal.get().getLineReader();
    private final Layout<ILoggingEvent> layout = new MachineLayout();

    @Override
    public void start() {
        super.start();
        layout.start();
    }

    @Override
    public void stop() {
        layout.stop();
        super.stop();
    }

    @Override
    protected void append(final ILoggingEvent event) {
        lineReader.printAbove(layout.doLayout(event));
    }

}