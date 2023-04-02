package org.machinemc.api.utils;

import org.jetbrains.annotations.Contract;

/**
 * Util for creating namespaced keys fast.
 * @see org.machinemc.api.utils.NamespacedKey
 */
public final class LazyNamespacedKey {

    /**
     * Creates namespaced key without parsing the input.
     * <p>
     * This part of the api is unsafe and can cause problems
     * if the provided string isn't a valid namespaced key.
     * @param namespacedKey unparsed namespaced key
     * @return namespaced key
     */
    @Contract("_ -> new")
    public static NamespacedKey lazy(String namespacedKey) {
        String[] parts = namespacedKey.split(":");
        StringBuilder key = new StringBuilder();
        for (int i = 1; i < parts.length; i++)
            key.append(parts[i]);
        return new NamespacedKey(parts[0], key.toString());
    }

    /**
     * Creates lazy namespaced key with 'minecraft' namespace
     * @param key key
     * @return namespaced key
     */
    public static NamespacedKey minecraft(String key) {
        return new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, key);
    }

    /**
     * Creates lazy namespaced key with 'machine' namespace
     * @param key key
     * @return namespaced key
     */
    public static NamespacedKey machine(String key) {
        return new NamespacedKey(NamespacedKey.MACHINE_NAMESPACE, key);
    }

}
