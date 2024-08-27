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
package org.machinemc.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

/**
 * Represents a thread with assigned ticker.
 */
public class TickThread extends Thread {

    private final Supplier<Ticker> ticker;

    protected TickThread(ThreadGroup group, Runnable task, Supplier<Ticker> ticker) {
        super(group, task);
        this.ticker = Preconditions.checkNotNull(ticker, "Ticker supplier can not be null");
    }

    /**
     * Returns the ticker associated with this thread.
     *
     * @return ticker
     */
    public Ticker getTicker() {
        final Ticker ticker = this.ticker.get();
        Preconditions.checkNotNull(ticker, "This tick thread has no assigned ticker to it");
        Preconditions.checkState(ticker.isTickThread(this), "Invalid ticker");
        return ticker;
    }

}
