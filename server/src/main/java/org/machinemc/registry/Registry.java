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
package org.machinemc.registry;

import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.barebones.key.NamespacedKey;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents an indexed registry containing objects
 * that may be retrieved by {@link org.machinemc.barebones.key.NamespacedKey}.
 * <p>
 * There are 2 types of registries, {@link BuiltInRegistry} and {@link DataDrivenRegistry}.
 * Built-in are loaded first and are hard-coded on the client.
 * Data-driven registries are fully created be the server and sent to the client.
 *
 * @param <T> registry entry type
 */
public sealed interface Registry<T> extends Iterable<T> permits BuiltInRegistry, DataDrivenRegistry, Registry.Writable {

    /**
     * Returns key of this registry.
     *
     * @return key of this registry
     */
    RegistryKey<T> key();

    /**
     * Returns numerical ID of registry entry.
     * <p>
     * Returns empty optional in case the provided entry is not
     * part of the registry.
     *
     * @param entry entry
     * @return its numerical id
     */
    OptionalInt getID(T entry);

    /**
     * Returns numerical ID of registry entry.
     *
     * @param entry entry
     * @return its numerical id
     * @throws java.util.NoSuchElementException if the provided entry
     * is not part of the registry
     */
    default int getIDOrThrow(T entry) {
        return getID(entry).orElseThrow();
    }

    /**
     * Returns entry by its id.
     * <p>
     * Returns empty optional in case there is no entry with
     * provided id.
     *
     * @param id id of the entry
     * @return entry with given id
     */
    Optional<T> getByID(int id);

    /**
     * Returns entry by its id.
     *
     * @param id id of the entry
     * @return entry with given id
     * @throws java.util.NoSuchElementException if there is no entry with
     * provided id
     */
    default T getByIDOrThrow(int id) {
        return getByID(id).orElseThrow();
    }

    /**
     * Returns entry with given key.
     * <p>
     * Returns empty optional in case there is no entry with
     * provided key.
     *
     * @param key key
     * @return entry
     */
    Optional<T> get(TypedKey<T> key);

    /**
     * Returns entry with given key.
     * <p>
     * Returns empty optional in case there is no entry with
     * provided key.
     *
     * @param key key
     * @return entry
     */
    default Optional<T> get(NamespacedKey key) {
        return get(new TypedKey<>(key(), key));
    }

    /**
     * Returns entry with given key.
     *
     * @param key key
     * @return entry
     * @throws java.util.NoSuchElementException if there is no entry with
     * provided key
     */
    default T getOrThrow(TypedKey<T> key) {
        return get(key).orElseThrow();
    }

    /**
     * Returns entry with given key.
     *
     * @param key key
     * @return entry
     * @throws java.util.NoSuchElementException if there is no entry with
     * provided key
     */
    default T getOrThrow(NamespacedKey key) {
        return get(key).orElseThrow();
    }

    /**
     * Returns key of given entry.
     * <p>
     * Returns empty optional in case the provided entry is not
     * part of the registry.
     *
     * @param entry entry
     * @return key of the entry
     */
    Optional<TypedKey<T>> getKey(T entry);

    /**
     * Returns key of given entry.
     *
     * @param entry entry
     * @return key of the entry
     */
    default TypedKey<T> getKeyOrThrow(T entry) {
        return getKey(entry).orElseThrow();
    }

    /**
     * Creates holder for entry with given key.
     *
     * @param key key
     * @return holder
     */
    default Optional<Holder<T>> getHolder(TypedKey<T> key) {
        if (!containsKey(key)) return Optional.empty();
        return Optional.of(getHolderOrThrow(key));
    }

    /**
     * Creates holder for entry with given key.
     *
     * @param key key
     * @return holder
     */
    default Optional<Holder<T>> getHolder(NamespacedKey key) {
        if (!containsKey(key)) return Optional.empty();
        return Optional.of(getHolderOrThrow(key));
    }

    /**
     * Creates holder for entry with given key.
     *
     * @param key key
     * @return holder
     * @throws java.util.NoSuchElementException if there is no entry with
     * provided key
     */
    default Holder<T> getHolderOrThrow(TypedKey<T> key) {
        return Holder.from(this, key);
    }

    /**
     * Creates holder for entry with given key.
     *
     * @param key key
     * @return holder
     * @throws java.util.NoSuchElementException if there is no entry with
     * provided key
     */
    default Holder<T> getHolderOrThrow(NamespacedKey key) {
        return getHolderOrThrow(new TypedKey<>(key(), key));
    }

    /**
     * Creates holder for given entry.
     *
     * @param entry entry
     * @return holder
     */
    default Optional<Holder<T>> getHolder(T entry) {
        if (!containsEntry(entry)) return Optional.empty();
        return Optional.of(getHolderOrThrow(entry));
    }

    /**
     * Creates holder for given entry.
     *
     * @param entry entry
     * @return holder
     * @throws java.util.NoSuchElementException if the entry is not present
     */
    default Holder<T> getHolderOrThrow(T entry) {
        return Holder.from(this, entry);
    }

    /**
     * Returns whether registry contains entry with given key.
     *
     * @param key key
     * @return whether the entry with given key is present
     */
    default boolean containsKey(TypedKey<T> key) {
        return get(key).isPresent();
    }

    /**
     * Returns whether registry contains entry with given key.
     *
     * @param key key
     * @return whether the entry with given key is present
     */
    default boolean containsKey(NamespacedKey key) {
        return get(key).isPresent();
    }

    /**
     * Returns whether registry given entry.
     *
     * @param entry entry
     * @return whether the entry is present
     */
    default boolean containsEntry(T entry) {
        return getKey(entry).isPresent();
    }

    /**
     * Returns size of the registry (number of registered entries).
     *
     * @return registry size
     */
    int size();

    /**
     * Returns view of the registry key set of this registry.
     *
     * @return key set of this registry
     * @see #keySet()
     */
    @UnmodifiableView Set<TypedKey<T>> registryKeySet();

    /**
     * Returns view of the key set of this registry.
     *
     * @return key set of this registry
     * @see #registryKeySet()
     */
    @UnmodifiableView Set<NamespacedKey> keySet();

    /**
     * Returns stream of elements in this registry.
     *
     * @return stream of elements in this registry
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Represents a registry that is modifiable.
     * <p>
     * This type of registry is available either during the server start
     * in case of built-in registries, or when data driven registry is unfrozen.
     *
     * @param <T> registry entry type
     */
    non-sealed interface Writable<T> extends Registry<T> {

        /**
         * Registers new entry to the registry.
         *
         * @param key entry key
         * @param entry entry
         * @throws IllegalStateException if entry with the given key already
         * exists in the registry
         */
        void register(TypedKey<T> key, T entry);

        /**
         * Registers new entry to the registry.
         *
         * @param key entry key
         * @param entry entry
         * @throws IllegalStateException if entry with the given key already
         * exists in the registry
         */
        default void register(NamespacedKey key, T entry) {
            register(new TypedKey<>(key(), key), entry);
        }

        /**
         * Unregisters entry with given key.
         *
         * @param key key
         * @return true if the entry was successfully unregistered or
         * false if it was not present
         */
        boolean unregister(TypedKey<T> key);

        /**
         * Unregisters entry with given key.
         *
         * @param key key
         * @return true if the entry was successfully unregistered or
         * false if it was not present
         */
        default boolean unregister(NamespacedKey key) {
            return unregister(new TypedKey<>(key(), key));
        }

        /**
         * Unregisters given entry.
         *
         * @param entry entry
         * @return true if the entry was successfully unregistered or
         * false if it was not present
         */
        default boolean unregister(T entry) {
            final TypedKey<T> key = getKey(entry).orElse(null);
            if (key == null) return false;
            return unregister(key);
        }

        /**
         * Returns the frozen registry created from this registry.
         * <p>
         * At the same time it freezes this writable registry and disables
         * any further modification.
         *
         * @return frozen registry
         */
        Registry<T> freeze();

        /**
         * Returns whether this writable registry allows further modifications
         * or whether it is frozen and can not be modified.
         *
         * @return whether the registry is frozen
         */
        boolean isFrozen();

    }

}
