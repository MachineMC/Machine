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
package org.machinemc.server.utils;

import com.google.common.cache.*;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.server.schedule.Scheduler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Cache implementation that saves the cached values for certain amount of time
 * and removes when they are both expired and no longer referenced in the code.
 * <p>
 * Should be used for weak value cache where computing the values costs too many
 * resources.
 * @param <K> key
 * @param <V> value
 */
public class WeaklyTimedCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> delegating;

    private final ScheduledExecutorService executor = new Scheduler(2).getThreadPoolExecutor();

    /**
     * For how long the value will stay referenced internally after loading.
     */
    private final long delay;
    private final TimeUnit referenceTime;

    public WeaklyTimedCache(final long delay, final TimeUnit referenceTime) {
        delegating = CacheBuilder.newBuilder()
                .weakValues()
                .build();
        this.delay = delay;
        this.referenceTime = Objects.requireNonNull(referenceTime);
    }

    /**
     * Creates nre weakly timed cache from an existing builder.
     * @param builder builder
     * @param delay delay of removing the entry after it's
     *              referenced only in the cache itself
     * @param referenceTime time unit for delay
     * @apiNote automatically assigns weak values to the provided builder
     */
    public WeaklyTimedCache(final CacheBuilder<K, V> builder, final long delay, final TimeUnit referenceTime) {
        delegating = builder
                .weakValues()
                .build();
        this.delay = delay;
        this.referenceTime = referenceTime;
    }

    @Override
    public @Nullable V getIfPresent(final @NotNull Object key) {
        final V value = delegating.getIfPresent(key);
        if (value == null) return null;
        executor.schedule(() -> eat(value), delay, referenceTime);
        return value;
    }

    @Override
    public @NotNull V get(final @NotNull K key, final @NotNull Callable<? extends V> valueLoader) throws ExecutionException {
        final V value = delegating.get(key, valueLoader);
        executor.schedule(() -> eat(value), delay, referenceTime);
        return value;
    }

    @Override
    public @NotNull ImmutableMap<K, V> getAllPresent(final @NotNull Iterable<?> keys) {
        final ImmutableMap<K, V> map = delegating.getAllPresent(keys);
        map.values().forEach(this::eat);
        return map;
    }

    @Override
    public void put(final @NotNull K key, final @NotNull V value) {
        delegating.put(key, value);
        executor.schedule(() -> eat(value), delay, referenceTime);
    }

    @Override
    public void putAll(final @NotNull Map<? extends K, ? extends V> m) {
        delegating.putAll(m);
        m.values().forEach(this::eat);
    }

    @Override
    public void invalidate(final @NotNull Object key) {
        delegating.invalidate(key);
    }

    @Override
    public void invalidateAll(final @NotNull Iterable<?> keys) {
        delegating.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        delegating.invalidateAll();
    }

    @Override
    public long size() {
        return delegating.size();
    }

    @Override
    public @NotNull CacheStats stats() {
        return delegating.stats();
    }

    @Override
    public @NotNull ConcurrentMap<K, V> asMap() {
        return delegating.asMap();
    }

    @Override
    public void cleanUp() {
        delegating.cleanUp();
    }

    private void eat(final @SuppressWarnings("unused") V value) {

    }

}
