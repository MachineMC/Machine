package me.pesekjak.machine.utils;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class NamespacedKey {

    public static final String MINECRAFT_KEY = "minecraft";
    public static final String MACHINE_KEY = "machine";

    private String namespace;
    private String key;

    public NamespacedKey(@NotNull String namespace, @NotNull String key) {
        if(!namespace.matches("[a-z0-9.-_]+") || !key.matches("[a-z0-9.-_/]+"))
            throw new RuntimeException("The key '" + namespace + ":" + key + "' doesn't match the identifier format.");
        this.namespace = namespace;
        this.key = key;
    }

    public static NamespacedKey parse(String namespacedKey) {
        int index = namespacedKey.indexOf(":");
        return new NamespacedKey(
                namespacedKey.substring(0, index),
                namespacedKey.substring(index + 1)
        );
    }

    public static NamespacedKey minecraft(@NotNull String key) {
        return new NamespacedKey(MINECRAFT_KEY, key);
    }

    public static NamespacedKey machine(@NotNull String key) {
        return new NamespacedKey(MACHINE_KEY, key);
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }

}
