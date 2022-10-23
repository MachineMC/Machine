package me.pesekjak.machine.utils;

import lombok.Data;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * An identifying object used to fetch and/or store unique objects,
 * NamespacedKey consists of namespace and key.
 * Valid characters for namespaces are [a-z0-9.-_].
 * Valid characters for keys are [a-z0-9.-_/].
 */
@Data
public class NamespacedKey {

    public static final String MINECRAFT_NAMESPACE = "minecraft";
    public static final String MACHINE_NAMESPACE = "machine";

    private static final String NAMESPACE_PATTERN = "[a-z0-9.-_]+";
    private static final String KEY_PATTERN = "[a-z0-9.-_/]+";

    private String namespace;
    private String key;

    public NamespacedKey(@NotNull String namespace, @NotNull String key) {
        if(!(namespace.matches(NAMESPACE_PATTERN) && key.matches(KEY_PATTERN)))
            throw new RuntimeException("The key '" + namespace + ":" + key + "' doesn't match the identifier format.");
        this.namespace = namespace;
        this.key = key;
    }

    /**
     * Parses the NamespacedKey from a String, namespace and key should
     * be separated by ':'.
     * @param namespacedKey String to parse as NamespacedKey
     * @return parsed NamespacedKey
     */
    public static NamespacedKey parse(@NotNull String namespacedKey) {
        int index = namespacedKey.indexOf(":");
        return new NamespacedKey(
                namespacedKey.substring(0, index),
                namespacedKey.substring(index + 1)
        );
    }

    /**
     * Returns Namespaced key with 'minecraft' namespace.
     * @param key key of the NamespacedKey
     * @return minecraft NamespacedKey
     */
    public static NamespacedKey minecraft(@NotNull String key) {
        return new NamespacedKey(MINECRAFT_NAMESPACE, key);
    }

    /**
     * Returns Namespaced key with 'machine' namespace.
     * @param key key of the NamespacedKey
     * @return machine NamespacedKey
     */
    public static NamespacedKey machine(@NotNull String key) {
        return new NamespacedKey(MACHINE_NAMESPACE, key);
    }

    /**
     * Converts adventure Key to NamespacedKey
     * @param key key to convert
     * @return converted NamespacedKey
     */
    public static NamespacedKey fromKey(@NotNull Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }

    /**
     * Converts the NamespacedKey to adventure Key
     * @return converted adventure Key
     */
    @SuppressWarnings("PatternValidation")
    public Key asKey() {
        return Key.key(namespace, key);
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }

}
