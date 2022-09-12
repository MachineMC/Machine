package me.pesekjak.machine.utils;

import lombok.Data;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

@Data
@SuppressWarnings("PatternValidation")
public class NamespacedKey {

    public static final String MINECRAFT_NAMESPACE = "minecraft";
    public static final String MACHINE_NAMESPACE = "machine";

    private static final String NAMESPACE_PATTERN = "[a-z0-9.-_]+";
    private static final String KEY_PATTERN = "[a-z0-9.-_/]+";

    private String namespace;
    private String key;

    public NamespacedKey(@NotNull @Pattern(NAMESPACE_PATTERN) String namespace, @NotNull @Pattern(KEY_PATTERN) String key) {
        if(!(namespace.matches(NAMESPACE_PATTERN) && key.matches(KEY_PATTERN)))
            throw new RuntimeException("The key '" + namespace + ":" + key + "' doesn't match the identifier format.");
        this.namespace = namespace;
        this.key = key;
    }

    public static NamespacedKey parse(@NotNull @Pattern(NAMESPACE_PATTERN + ":" + KEY_PATTERN) String namespacedKey) {
        int index = namespacedKey.indexOf(":");
        return new NamespacedKey(
                namespacedKey.substring(0, index),
                namespacedKey.substring(index + 1)
        );
    }

    public static NamespacedKey minecraft(@NotNull @Pattern(KEY_PATTERN) String key) {
        return new NamespacedKey(MINECRAFT_NAMESPACE, key);
    }

    public static NamespacedKey machine(@NotNull @Pattern(KEY_PATTERN) String key) {
        return new NamespacedKey(MACHINE_NAMESPACE, key);
    }

    public static NamespacedKey fromKey(@NotNull Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }

    public Key asKey() {
        return Key.key(namespace, key);
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }

}
