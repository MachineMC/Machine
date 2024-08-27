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

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Completable future that can complete on the ticker
 * thread if there is one available in the context.
 *
 * @param <T> return type
 */
public class TickerAwareFuture<T> extends CompletableFuture<T> {

    private final @Nullable Ticker ticker;

    public TickerAwareFuture() {
        this(Ticker.current().orElse(null));
    }

    public TickerAwareFuture(final @Nullable Ticker ticker) {
        this.ticker = ticker;
    }

    /**
     * Completes the future on its assigned ticker.
     *
     * @param value value
     * @return when the ticker completes the future
     */
    public CompletableFuture<T> completeOnTicker(final T value) {
        if (ticker == null) {
            complete(value);
            return CompletableFuture.completedFuture(value);
        }
        return completeAsync(() -> value, ticker.getTickThreadExecutor());
    }

}
