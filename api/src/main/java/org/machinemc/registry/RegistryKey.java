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
import org.machinemc.barebones.key.NamespacedKey;

/**
 * Represents a key of a server registry.
 *
 * @param key registry key
 * @param <T> registry entry type
 * @param <R> type of the registry
 */
public record RegistryKey<T, R extends Registry<T>>(NamespacedKey key) {

    public RegistryKey {
        Preconditions.checkNotNull(key, "Registry key can not be null");
    }

}
