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
 * Represents a key of an entry in a registry.
 *
 * @param registryKey key of source registry
 * @param key key of the entry
 * @param <T> registry entry type
 */
public record TypedKey<T>(RegistryKey<T> registryKey, NamespacedKey key) {

    public TypedKey {
        Preconditions.checkNotNull(registryKey, "Registry key can not be null");
        Preconditions.checkNotNull(key, "Entry key can not be null");
    }

}
