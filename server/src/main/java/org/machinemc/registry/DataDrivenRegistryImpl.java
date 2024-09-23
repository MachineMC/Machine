package org.machinemc.registry;

import com.google.common.base.Preconditions;
import lombok.Locked;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.barebones.key.NamespacedKey;

import java.util.*;
import java.util.function.Consumer;

public class DataDrivenRegistryImpl<T> implements DataDrivenRegistry<T> {

    private final RegistryKey<T, DataDrivenRegistry<T>> key;
    private final Map<TypedKey<T>, T> entries = Collections.synchronizedMap(new LinkedHashMap<>());

    public DataDrivenRegistryImpl(final RegistryKey<T, DataDrivenRegistry<T>> key) {
        this.key = key;
    }

    @Override
    public RegistryKey<T, DataDrivenRegistry<T>> key() {
        return key;
    }

    @Override
    public OptionalInt getID(final T entry) {
        int id = 0;
        for (final T value : entries.values()) {
            if (value.equals(entry))
                return OptionalInt.of(id);
            id++;
        }
        return OptionalInt.empty();
    }

    @Override
    public Optional<T> getByID(int id) {
        int currentID = 0;
        for (final T value : entries.values()) {
            if (currentID++ == id)
                return Optional.of(value);
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> get(final TypedKey<T> key) {
        return Optional.ofNullable(entries.get(key));
    }

    @Override
    public Optional<TypedKey<T>> getKey(final T entry) {
        for (final Map.Entry<TypedKey<T>, T> mapEntry : entries.entrySet()) {
            if (mapEntry.getValue().equals(entry))
                return Optional.of(mapEntry.getKey());
        }
        return Optional.empty();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public @Unmodifiable Set<TypedKey<T>> registryKeySet() {
        return Set.copyOf(entries.keySet());
    }

    @Override
    public @Unmodifiable Set<NamespacedKey> keySet() {
        final Set<NamespacedKey> keys = new HashSet<>();
        for (final TypedKey<T> key : entries.keySet())
            keys.add(key.key());
        return Collections.unmodifiableSet(keys);
    }

    @Locked
    @Override
    public void modify(final Consumer<Writable<T>> consumer) {
        final WritableImpl writable = new WritableImpl();
        consumer.accept(writable);
        writable.freeze();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return entries.values().iterator();
    }

    public class WritableImpl implements Writable<T> {

        private final Map<TypedKey<T>, T> entries = Collections.synchronizedMap(new LinkedHashMap<>(DataDrivenRegistryImpl.this.entries));
        private boolean frozen;

        @Override
        public RegistryKey<T, DataDrivenRegistry<T>> key() {
            return DataDrivenRegistryImpl.this.key();
        }

        @Override
        public OptionalInt getID(final T entry) {
            return DataDrivenRegistryImpl.this.getID(entry);
        }

        @Override
        public Optional<T> getByID(final int id) {
            return DataDrivenRegistryImpl.this.getByID(id);
        }

        @Override
        public Optional<T> get(final TypedKey<T> key) {
            return Optional.ofNullable(entries.get(key));
        }

        @Override
        public Optional<TypedKey<T>> getKey(final T entry) {
            for (final Map.Entry<TypedKey<T>, T> mapEntry : entries.entrySet()) {
                if (mapEntry.getValue().equals(entry))
                    return Optional.of(mapEntry.getKey());
            }
            return Optional.empty();
        }

        @Override
        public int size() {
            return entries.size();
        }

        @Override
        public @Unmodifiable Set<TypedKey<T>> registryKeySet() {
            return Set.copyOf(entries.keySet());
        }

        @Override
        public @Unmodifiable Set<NamespacedKey> keySet() {
            final Set<NamespacedKey> keys = new HashSet<>();
            for (final TypedKey<T> key : entries.keySet())
                keys.add(key.key());
            return Collections.unmodifiableSet(keys);
        }

        @Override
        public void modify(final Consumer<DataDrivenRegistry.Writable<T>> consumer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void register(final TypedKey<T> key, final T entry) {
            checkFrozen();
            Preconditions.checkState(!entries.containsKey(key), "There is already an entry with key: " + key);
            entries.put(key, entry);
        }

        @Override
        public boolean unregister(final TypedKey<T> key) {
            checkFrozen();
            return entries.remove(key) != null;
        }

        @Override
        public @NotNull Iterator<T> iterator() {
            return entries.values().iterator();
        }

        public void freeze() {
            frozen = true;
            DataDrivenRegistryImpl.this.entries.clear();
            DataDrivenRegistryImpl.this.entries.putAll(entries);
        }

        private void checkFrozen() {
            if (frozen)
                throw new IllegalStateException("This writable registry instance is already frozen");
        }

    }

}
