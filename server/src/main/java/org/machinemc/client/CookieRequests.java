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
package org.machinemc.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import lombok.Locked;
import org.machinemc.annotation.TickerAware;
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.client.cookie.Cookie;
import org.machinemc.network.protocol.cookie.serverbound.C2SCookieResponsePacket;
import org.machinemc.server.TickerAwareFuture;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class managing cookie requests.
 *
 * @see org.machinemc.client.cookie.Cookie
 */
public final class CookieRequests {

    private final Map<NamespacedKey, Collection<TickerAwareFuture<Optional<Cookie>>>> requests = new ConcurrentHashMap<>();

    /**
     * Creates new completable future for cookie request.
     * <p>
     * It is completed once {@link #onResponse(C2SCookieResponsePacket)} with the
     * same channel is called.
     *
     * @param channel cookie key
     * @return completable future
     */
    @Locked
    @TickerAware
    public CompletableFuture<Optional<Cookie>> create(final NamespacedKey channel) {
        Collection<TickerAwareFuture<Optional<Cookie>>> existing;

        if ((existing = requests.get(channel)) != null) {
            final TickerAwareFuture<Optional<Cookie>> future = new TickerAwareFuture<>();
            existing.add(future);
            return future;
        }

        existing = Collections.newSetFromMap(CacheBuilder.newBuilder()
                .weakKeys()
                .removalListener((RemovalListener<TickerAwareFuture<Optional<Cookie>>, Boolean>) notification -> {
                    if (!requests.getOrDefault(channel, Collections.emptyList()).isEmpty()) return;
                    requests.remove(channel);
                })
                .build()
                .asMap());
        requests.put(channel, existing);
        return create(channel);
    }

    /**
     * Completes all the futures awaiting cookie response on this channel.
     *
     * @param response cookie response
     */
    @Locked
    public void onResponse(final C2SCookieResponsePacket response) {
        final Collection<TickerAwareFuture<Optional<Cookie>>> requests = this.requests.get(response.getChannel());
        if (requests == null || requests.isEmpty()) return;
        final Optional<Cookie> cookie = response.getCookie();
        requests.forEach(future -> future.completeOnTicker(cookie));
        requests.clear();
    }

}
