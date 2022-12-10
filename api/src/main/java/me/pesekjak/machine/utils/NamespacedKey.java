package me.pesekjak.machine.utils;

import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.RegExp;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * An identifying object used to fetch and/or store unique objects,
 * NamespacedKey consists of namespace and key.
 * <p>
 * Valid characters for namespaces are [a-z0-9.-_].
 * <p>
 * Valid characters for keys are [a-z0-9.-_/].
 * @param namespace namespace of the namespace key
 * @param key key of the namespace key
 */
public record NamespacedKey(
        @org.intellij.lang.annotations.Pattern(NAMESPACE_REGEX) @NotNull String namespace,
        @org.intellij.lang.annotations.Pattern(KEY_REGEX) @NotNull String key) {

    public static final String
            MINECRAFT_NAMESPACE = "minecraft",
            MACHINE_NAMESPACE = "machine";

    @RegExp
    public static final @NotNull String
            NAMESPACE_REGEX = "[a-z0-9.-_]+",
            KEY_REGEX = "[a-z0-9.-_/]+";

    private static final @NotNull Pattern
            NAMESPACE_PATTERN = Pattern.compile(NAMESPACE_REGEX),
            KEY_PATTERN = Pattern.compile(KEY_REGEX);

    @Contract("_, _ -> new")
    public static @NotNull NamespacedKey of(@Subst("machine") @org.intellij.lang.annotations.Pattern(NAMESPACE_REGEX) String namespace, @Subst("server") @org.intellij.lang.annotations.Pattern(KEY_REGEX) String key) {
        return new NamespacedKey(namespace, key);
    }

    public NamespacedKey(@org.intellij.lang.annotations.Pattern(NAMESPACE_REGEX) @NotNull String namespace, @org.intellij.lang.annotations.Pattern(KEY_REGEX) @NotNull String key) {
        if (!(NAMESPACE_PATTERN.matcher(namespace).matches() && KEY_PATTERN.matcher(key).matches()))
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
    @Contract("_ -> new")
    public static @NotNull NamespacedKey parse(@Subst("machine:server") @org.intellij.lang.annotations.Pattern("[a-z0-9.-_]+:[a-z0-9.-_/]+") @NotNull String namespacedKey) {
        int index = namespacedKey.indexOf(":");
        final @Subst("machine") String namespace = namespacedKey.substring(0, index);
        final @Subst("server") String key = namespacedKey.substring(index + 1);
        return new NamespacedKey(
                namespace,
                key
        );
    }

    /**
     * Returns Namespaced key with 'minecraft' namespace.
     * @param key key of the NamespacedKey
     * @return minecraft NamespacedKey
     */
    @Contract("_ -> new")
    public static @NotNull NamespacedKey minecraft(@Subst("server") @org.intellij.lang.annotations.Pattern(KEY_REGEX) @NotNull String key) {
        return new NamespacedKey(MINECRAFT_NAMESPACE, key);
    }

    /**
     * Returns Namespaced key with 'machine' namespace.
     * @param key key of the NamespacedKey
     * @return machine NamespacedKey
     */
    @Contract("_ -> new")
    public static @NotNull NamespacedKey machine(@Subst("server") @org.intellij.lang.annotations.Pattern(KEY_REGEX) @NotNull String key) {
        return new NamespacedKey(MACHINE_NAMESPACE, key);
    }

    /**
     * Converts adventure Key to NamespacedKey
     * @param key key to convert
     * @return converted NamespacedKey
     */
    @SuppressWarnings("PatternValidation")
    @Contract("_ -> new")
    public static @NotNull NamespacedKey fromKey(@NotNull Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }

    /**
     * Converts the NamespacedKey to adventure Key
     * @return converted adventure Key
     */
    @SuppressWarnings("PatternValidation")
    @Contract(pure = true)
    public @NotNull Key asKey() {
        return Key.key(namespace, key);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return namespace + ":" + key;
    }

}
