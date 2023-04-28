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
package org.machinemc.api.utils;

import org.jetbrains.annotations.Contract;

/**
 * Util for creating namespaced keys fast.
 * @see org.machinemc.api.utils.NamespacedKey
 */
public final class LazyNamespacedKey {

    private LazyNamespacedKey() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates namespaced key without parsing the input.
     * <p>
     * This part of the api is unsafe and can cause problems
     * if the provided string isn't a valid namespaced key.
     * @param namespacedKey unparsed namespaced key
     * @return namespaced key
     */
    @Contract("_ -> new")
    public static NamespacedKey lazy(final String namespacedKey) {
        final String[] parts = namespacedKey.split(":");
        final StringBuilder key = new StringBuilder();
        for (int i = 1; i < parts.length; i++)
            key.append(parts[i]);
        return new NamespacedKey(parts[0], key.toString());
    }

    /**
     * Creates lazy namespaced key with 'minecraft' namespace.
     * @param key key
     * @return namespaced key
     */
    public static NamespacedKey minecraft(final String key) {
        return new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, key);
    }

    /**
     * Creates lazy namespaced key with 'machine' namespace.
     * @param key key
     * @return namespaced key
     */
    public static NamespacedKey machine(final String key) {
        return new NamespacedKey(NamespacedKey.MACHINE_NAMESPACE, key);
    }

}
