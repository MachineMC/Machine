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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Contract;
import org.machinemc.barebones.key.NamespacedKey;
import org.machinemc.registry.DataDrivenRegistry;
import org.machinemc.registry.RegistryKey;
import org.machinemc.registry.TypedKey;

import java.util.*;

/**
 * Represents a data pack running on the server.
 * <p>
 * This should not be confused with vanilla server data packs. Data packs in Machine
 * are holders of data shared between the client and the server, used to reduce the
 * required game information sent during the client configuration, on Notchian
 * server these are also known as 'KnownPacks'.
 *
 * @param info data pack information
 * @param entries contents of this data pack
 */
public record DataPack(Info info, Map<RegistryKey<?, ? extends DataDrivenRegistry<?>>, Resources<?>> entries) {

    /**
     * Creates new empty data pack from a key and a version.
     *
     * @param key key
     * @param version version
     * @return data pack
     */
    public static DataPack create(final NamespacedKey key, final String version) {
        return new DataPack(new Info(key, version), Collections.emptyMap());
    }

    public DataPack {
        Preconditions.checkNotNull(info, "Data pack info can not be null");
        Preconditions.checkNotNull(entries, "Data pack entries can not be null");
        entries.keySet().removeIf(Objects::isNull);
        entries = Collections.unmodifiableMap(entries);
    }

    /**
     * Returns resources for given registry in this data pack.
     *
     * @param key key
     * @return resources in this data pack for given key
     * @param <T> registry type
     */
    @SuppressWarnings("unchecked")
    public <T> Resources<T> get(RegistryKey<T, ? extends DataDrivenRegistry<T>> key) {
        return entries.containsKey(key) ? (Resources<T>) entries.get(key) : Resources.empty();
    }

    @Contract("_, _ -> new")
    public <T> DataPack addEntries(final RegistryKey<T, ? extends DataDrivenRegistry<T>> registry, Resources<T> resources) {
        Resources<T> existing = get(registry);
        existing = existing.add(resources);
        return new DataPack(
                info,
                ImmutableMap.<RegistryKey<?, ? extends DataDrivenRegistry<?>>, Resources<?>>builder()
                        .putAll(entries)
                        .put(registry, existing)
                        .build()
        );
    }

    /**
     * Information about a data pack.
     *
     * @param key name of the data pack
     * @param version version of the data pack, e.g. {@code 1.21.1}
     */
    public record Info(NamespacedKey key, String version) {

        public Info {
            Preconditions.checkNotNull(key, "Data pack key can not be null");
            Preconditions.checkNotNull(version, "Data pack version can not be null");
        }

        @Override
        public String toString() {
            return key + ":" + version;
        }

    }

    /**
     * Represents resources available in a data pack.
     *
     * @param keys available keys
     * @param <T> resource type
     */
    public record Resources<T>(TypedKey<T>... keys) {

        /**
         * Return new empty resources.
         *
         * @return empty resources
         * @param <T> resource type
         */
        @SuppressWarnings("unchecked")
        public static <T> Resources<T> empty() {
            return new Resources<>();
        }

        @SuppressWarnings("unchecked")
        public Resources {
            Preconditions.checkNotNull(keys, "Resources keys can not be null");
            keys = Arrays.stream(keys).filter(Objects::nonNull).distinct().toArray(TypedKey[]::new);
        }

        @SuppressWarnings("unchecked")
        public Resources(final Collection<TypedKey<T>> keys) {
            this(keys.toArray(TypedKey[]::new));
        }

        /**
         * Creates copy of these resources and adds new keys to it.
         *
         * @param keys keys to add
         * @return new resources
         */
        @Contract("_ -> new")
        public Resources<T> add(final Collection<TypedKey<T>> keys) {
            Preconditions.checkNotNull(keys, "Keys to add can not be null");
            return new Resources<>(ImmutableList.<TypedKey<T>>builder().add(this.keys).addAll(keys).build());
        }

        /**
         * Creates copy of these resources and adds new keys to it.
         *
         * @param keys keys to add
         * @return new resources
         */
        @SafeVarargs
        @Contract("_ -> new")
        public final Resources<T> add(final TypedKey<T>... keys) {
            return add(List.of(keys));
        }

        /**
         * Creates copy of these resources and adds new keys from
         * provided resources to it.
         *
         * @param other resources to add
         * @return new resources
         */
        public Resources<T> add(Resources<T> other) {
            return add(other.keys);
        }

    }

}
