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

import com.google.common.base.Preconditions;

/**
 * Represents registry entry with associated key.
 *
 * @param key key
 * @param value value
 * @param <T> entry type
 */
public record Holder<T>(TypedKey<T> key, T value) {

    public Holder {
        Preconditions.checkNotNull(key, "Holder key can not be null");
        Preconditions.checkNotNull(value, "Holder value can not be null");
    }

    /**
     * Returns holder from registry and entry key.
     *
     * @param registry registry
     * @param key entry key
     * @return holder for given entry
     * @param <T> entry type
     * @throws java.util.NoSuchElementException if there is no entry with
     * provided key in given registry
     */
    public static <T> Holder<T> from(final Registry<T> registry, final TypedKey<T> key) {
        return new Holder<>(key, registry.getOrThrow(key));
    }

    /**
     * Returns holder from registry and its entry.
     *
     * @param registry registry
     * @param entry registry entry
     * @return holder for given entry
     * @param <T> entry type
     * @throws java.util.NoSuchElementException if there is no entry with
     * provided key in given registry
     */
    public static <T> Holder<T> from(final Registry<T> registry, final T entry) {
        return new Holder<>(registry.getKeyOrThrow(entry), entry);
    }

}
