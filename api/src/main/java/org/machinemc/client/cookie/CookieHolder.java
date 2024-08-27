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
package org.machinemc.client.cookie;

import org.machinemc.annotation.TickerAware;
import org.machinemc.barebones.key.NamespacedKey;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an entity capable of storing pieces of information as cookies.
 * <p>
 * Each cookie is bound to a {@link NamespacedKey}.
 * <p>
 * Cookies persist between server transfers.
 * The Notchian client only accepts cookies of up to 5 kiB in size.
 */
public interface CookieHolder {

    /**
     * Requests cookie from a client.
     * <p>
     * Returns empty optional if there is no such a cookie
     * stored in the client.
     *
     * @param cookie requested cookie
     * @return cookie stored on the client
     */
    @TickerAware
    default CompletableFuture<Optional<Cookie>> requestCookie(Cookie cookie) {
        return requestCookie(cookie.key());
    }

    /**
     * Requests cookie from a client.
     * <p>
     * Returns empty optional if there is no such a cookie
     * stored in the client.
     *
     * @param key key of the cookie
     * @return cookie stored on the client
     */
    @TickerAware
    CompletableFuture<Optional<Cookie>> requestCookie(NamespacedKey key);

    /**
     * Stores cookie on the client.
     * <p>
     * Returns previously stored cookie with the same key.
     *
     * @param cookie cookie to store
     * @return previously stored cookie with the same key
     */
    @TickerAware
    CompletableFuture<Optional<Cookie>> storeCookie(Cookie cookie);

}
