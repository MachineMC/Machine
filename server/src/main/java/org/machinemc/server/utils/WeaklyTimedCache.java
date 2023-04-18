package org.machinemc.server.utils;

import com.google.common.cache.*;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.server.schedule.Scheduler;

import java.util.concurrent.*;

/**
 * Cache implementation that saves the cached values for certain amount of time
 * and removes when they are both expired and not longer referenced in the code.
 * <p>
 * Should be used for weak value cache where computing the values costs too many
 * resources.
 * @param <K> key
 * @param <V> value
 */
@SuppressWarnings("UnstableApiUsage")
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
        this.referenceTime = referenceTime;
    }

    /**
     * Creates nre weakly timed cache from an exisiting builder.
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
    public @Nullable V getIfPresent(final @NotNull K key) {
        final V value = delegating.getIfPresent(key);
        if (value == null) return null;
        executor.schedule(() -> eat(value), delay, referenceTime);
        return value;
    }

    @Override
    public V get(final @NotNull K key, final @NotNull Callable<? extends V> valueLoader) throws ExecutionException {
        final V value = delegating.get(key, valueLoader);
        executor.schedule(() -> eat(value), delay, referenceTime);
        return value;
    }

    @Override
    @Deprecated
    public V get(final @NotNull K key) throws ExecutionException {
        final V value = delegating.get(key);
        if (value == null) return null;
        executor.schedule(() -> eat(value), delay, referenceTime);
        return value;
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(final @NotNull Iterable<? extends K> keys) {
        return delegating.getAllPresent(keys);
    }

    @Override
    public void put(final @NotNull K key, final @NotNull V value) {
        delegating.put(key, value);
        executor.schedule(() -> eat(value), delay, referenceTime);
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
    public CacheStats stats() {
        return delegating.stats();
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        return delegating.asMap();
    }

    @Override
    public void cleanUp() {
        delegating.cleanUp();
    }

    @Override
    @Deprecated
    public V getUnchecked(final @NotNull K key) {
        final V value = delegating.getUnchecked(key);
        if (value == null) return null;
        executor.schedule(() -> eat(value), delay, referenceTime);
        return value;
    }

    @Override
    @Deprecated
    public V apply(final @NotNull K key) {
        final V value = delegating.apply(key);
        if (value == null) return null;
        executor.schedule(() -> eat(value), delay, referenceTime);
        return value;
    }

    private void eat(final @SuppressWarnings("unused") V value) {

    }

}
