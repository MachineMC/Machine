package org.machinemc.registry;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.barebones.key.NamespacedKey;

import java.util.*;

public class BuiltInRegistryImpl<T> implements BuiltInRegistry<T> {

    private final RegistryKey<T, BuiltInRegistry<T>> key;
    private final Map<TypedKey<T>, T> entries = new LinkedHashMap<>();

    private boolean frozen = false;

    public BuiltInRegistryImpl(final RegistryKey<T, BuiltInRegistry<T>> key) {
        this.key = key;
    }

    @Override
    public RegistryKey<T, BuiltInRegistry<T>> key() {
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
    public Optional<T> getByID(final int id) {
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

    @Override
    public @NotNull Iterator<T> iterator() {
        return entries.values().iterator();
    }

    /**
     * Registers new entry to the registry.
     *
     * @param key entry key
     * @param entry entry
     * @throws IllegalStateException if entry with the given key already
     * exists in the registry
     */
    public void register(final TypedKey<T> key, final T entry) {
        Preconditions.checkState(!frozen, "A built-in registry can not be modified once it is loaded");
        Preconditions.checkState(!entries.containsKey(key), "Entry with the given key already exists in the registry");
        entries.put(key, entry);
    }

    /**
     * Registers new entry to the registry.
     *
     * @param key entry key
     * @param entry entry
     * @throws IllegalStateException if entry with the given key already
     * exists in the registry
     */
    public void register(final NamespacedKey key, final T entry) {
        register(new TypedKey<>(key(), key), entry);
    }

    /**
     * Freezes the registry.
     * <p>
     * Once the registry is frozen, it can not be modified.
     *
     * @throws IllegalStateException if the registry is already frozen
     */
    public void freeze() {
        Preconditions.checkState(!frozen, "A built-in registry can not be modified once it is loaded");
        frozen = true;
    }

}
