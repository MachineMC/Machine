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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.util.CachingDateFormatter;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;

import java.util.Map;

/**
 * Log message layout used by Machine server.
 */
public class MachineLayout extends LayoutBase<ILoggingEvent> {

    private final CachingDateFormatter dateFormatter = new CachingDateFormatter("HH:mm:ss");
    private final ThrowableProxyConverter tpc = new ThrowableProxyConverter();

    private final Map<Level, Colour> levelColors = Map.of(
            Level.WARN, ChatColor.YELLOW,
            Level.ERROR, ChatColor.RED
    );

    @Override
    public void start() {
        super.start();
        tpc.start();
    }

    @Override
    public void stop() {
        super.stop();
        tpc.stop();
    }

    @Override
    public String doLayout(final ILoggingEvent event) {
        if (!this.isStarted()) return "";

        final StringBuilder builder = new StringBuilder();

        builder.append(getColor(event.getLevel()));

        builder.append("[")
                .append(dateFormatter.format(event.getTimeStamp()))
                .append(" ")
                .append(event.getLevel().toString())
                .append("]: ");

        if (!event.getLoggerName().equals(ServerTerminal.logger().getName())) {
            builder.append("[")
                    .append(event.getLoggerName())
                    .append("] ");
        }

        builder.append(event.getFormattedMessage());

        builder.append(CoreConstants.LINE_SEPARATOR);

        final IThrowableProxy throwable = event.getThrowableProxy();
        if (throwable != null)
            builder.append(tpc.convert(event));

        builder.append(resetColor());

        return builder.toString();
    }

    private String getColor(final Level level) {
        if (!levelColors.containsKey(level)) return "";
        return levelColors.get(level).getConsoleFormat();
    }

    private String resetColor() {
        return ChatColor.RESET.getConsoleFormat();
    }

}
