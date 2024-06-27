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
import org.slf4j.Logger;

/**
 * Thread group handling exceptions with provided logger.
 */
public class LoggingThreadGroup extends ThreadGroup {

    private final Logger logger;

    public LoggingThreadGroup(final ThreadGroup parent, final String name, final Logger logger) {
        super(parent, name);
        this.logger = Preconditions.checkNotNull(logger, "Logger can not be null");
    }

    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        logger.error("Thread {} generated unhandled exception", t.getName(), e);
    }

}
