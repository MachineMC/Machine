package me.pesekjak.machine.utils;

import org.jetbrains.annotations.Contract;

/**
 * @see me.pesekjak.machine.utils.NamespacedKey
 */
public class LazyNamespacedKey extends NamespacedKey {

    private LazyNamespacedKey(String namespace, String key) {
        super(namespace, key);
    }


    @Contract("_, _ -> new")
    public static NamespacedKey of(String namespace, String key) {
        return new LazyNamespacedKey(namespace, key);
    }

    /**
     * Parses the NamespacedKey from a String, namespace and key should
     * be separated by ':'.
     * @param namespacedKey String to parse as NamespacedKey
     * @return parsed NamespacedKey
     */
    @Contract("_ -> new")
    public static NamespacedKey parse(String namespacedKey) {
        String[] key = parseNamespacedKey(namespacedKey);
        if (key == null)
            throw new IllegalArgumentException("The namespaced key '" + namespacedKey + "' does not have a separator character (':')");

        return LazyNamespacedKey.of(key[0], key[1]);
    }

    /**
     * Returns Namespaced key with 'minecraft' namespace.
     * @param key key of the NamespacedKey
     * @return minecraft NamespacedKey
     */
    @Contract("_ -> new")
    public static NamespacedKey minecraft(String key) {
        return LazyNamespacedKey.of(MINECRAFT_NAMESPACE, key);
    }

    /**
     * Returns Namespaced key with 'machine' namespace.
     * @param key key of the NamespacedKey
     * @return machine NamespacedKey
     */
    @Contract("_ -> new")
    public static NamespacedKey machine(String key) {
        return LazyNamespacedKey.of(MACHINE_NAMESPACE, key);
    }

}
