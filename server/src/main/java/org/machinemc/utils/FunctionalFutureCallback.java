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
package org.machinemc.utils;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FutureCallback;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Util for simple creation of future callbacks.
 */
public final class FunctionalFutureCallback {

    private FunctionalFutureCallback() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates new future call back from two consumers.
     *
     * @param onSuccess runs on success
     * @param onFailure runs on failure
     * @return new future callback using the two consumers
     * @param <T> type on success
     */
    public static <T> FutureCallback<T> create(final Consumer<T> onSuccess, final Consumer<Throwable> onFailure) {
        Preconditions.checkNotNull(onSuccess);
        Preconditions.checkNotNull(onFailure);
        return new FutureCallback<>() {
            @Override
            public void onSuccess(final T result) {
                onSuccess.accept(result);
            }

            @Override
            public void onFailure(final @NotNull Throwable throwable) {
                onFailure.accept(throwable);
            }
        };
    }

}
