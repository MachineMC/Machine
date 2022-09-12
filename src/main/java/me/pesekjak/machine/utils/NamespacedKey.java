package me.pesekjak.machine.utils;

import lombok.Data;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

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

    public static NamespacedKey parse(@NotNull String namespacedKey) {
        int index = namespacedKey.indexOf(":");
        return new NamespacedKey(
                namespacedKey.substring(0, index),
                namespacedKey.substring(index + 1)
        );
    }

    public static NamespacedKey minecraft(@NotNull String key) {
        return new NamespacedKey(MINECRAFT_NAMESPACE, key);
    }

    public static NamespacedKey machine(@NotNull String key) {
        return new NamespacedKey(MACHINE_NAMESPACE, key);
    }

    public static NamespacedKey fromKey(@NotNull Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }

    @SuppressWarnings("PatternValidation")
    public Key asKey() {
        return Key.key(namespace, key);
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }

}
