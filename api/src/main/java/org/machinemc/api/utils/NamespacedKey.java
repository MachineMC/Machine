package org.machinemc.api.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An identifying object used to fetch and/or store unique objects,
 * NamespacedKey consists of namespace and key.
 * <p>
 * Valid characters for namespaces are [a-z0-9.-_].
 * <p>
 * Valid characters for keys are [a-z0-9.-_/].
 */
public final class NamespacedKey {

    public static final String
            MINECRAFT_NAMESPACE = "minecraft",
            MACHINE_NAMESPACE = "machine";

    private final String namespace;
    private final String key;

    protected NamespacedKey(final String namespace, final String key) {
        this.namespace = namespace;
        this.key = key;
    }

    /**
     * Creates new namespaced key from given namespace and key.
     * @param namespace namespace
     * @param key key
     * @return new namespaced key
     */
    @Contract("_, _ -> new")
    public static NamespacedKey of(final String namespace, final String key) {
        if (!isValidNamespacedKey(namespace, key))
            throw new IllegalArgumentException("The key '" + namespace + ":" + key + "' "
                    + "doesn't match the identifier format.");
        return new NamespacedKey(namespace, key);
    }

    /**
     * Parses the NamespacedKey from a String, namespace and key should
     * be separated by ':'.
     * @param namespacedKey String to parse as NamespacedKey
     * @return parsed NamespacedKey
     */
    @Contract("_ -> new")
    public static NamespacedKey parse(final String namespacedKey) {
        String[] key = parseNamespacedKey(namespacedKey);
        if (key == null)
            throw new IllegalArgumentException("The namespaced key '" + namespacedKey + "' "
                    + "does not have a separator character ':'");

        return NamespacedKey.of(key[0], key[1]);
    }

    /**
     * Returns Namespaced key with 'minecraft' namespace.
     * @param key key of the NamespacedKey
     * @return minecraft NamespacedKey
     */
    @Contract("_ -> new")
    public static NamespacedKey minecraft(final String key) {
        return NamespacedKey.of(MINECRAFT_NAMESPACE, key);
    }

    /**
     * Returns Namespaced key with 'machine' namespace.
     * @param key key of the NamespacedKey
     * @return machine NamespacedKey
     */
    @Contract("_ -> new")
    public static NamespacedKey machine(final String key) {
        return NamespacedKey.of(MACHINE_NAMESPACE, key);
    }

    @Override
    @Contract(pure = true)
    public String toString() {
        return namespace + ":" + key;
    }

    /**
     * @return namespace of the namespaced key
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return key of the namespaced key
     */
    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NamespacedKey) obj;
        return Objects.equals(this.namespace, that.namespace)
                && Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key);
    }

    /**
     * Parses a string into a key-value pair.
     * <p>
     * This doesn't check if the pair follows the namespaced key format,
     * use {@link NamespacedKey#isValidNamespacedKey(String, String)} to check.
     * @param input the input
     * @return a string array where the first value is the namespace and the second value is the namespace,
     * or null if that input doesn't have a separator character ':'
     */
    protected static String @Nullable [] parseNamespacedKey(final String input) {
        String[] namespacedKey = new String[2];
        char[] chars = input.toCharArray();
        StringBuilder builder = new StringBuilder();
        boolean separator = false;
        for (char c : chars) {
            if (c == ':') {
                separator = true;
                namespacedKey[0] = builder.toString();
                builder = new StringBuilder();
                continue;
            }

            builder.append(c);
        }
        if (!separator)
            return null;
        namespacedKey[1] = builder.toString();
        return namespacedKey;
    }

    /**
     * Valid characters for namespaces are [a-z0-9.-_].
     * <p>
     * Valid characters for keys are [a-z0-9.-_/].
     * @param namespace the namespace
     * @param key the key
     * @return whether the namespace and key follow their formats
     */
    protected static boolean isValidNamespacedKey(final String namespace, final String key) {
        if (namespace.isEmpty())
            return false;
        for (char c : namespace.toCharArray()) {
            if (!isValidNamespace(c))
                return false;
        }
        if (key.isEmpty())
            return false;
        for (char c : key.toCharArray()) {
            if (!isValidKey(c))
                return false;
        }
        return true;
    }

    /**
     * Valid characters for namespaces are [a-z0-9.-_].
     * @param c the character
     * @return whether character is allowed in a namespace
     */
    protected static boolean isValidNamespace(final char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '.' || c == '-' || c == '_';
    }

    /**
     * Valid characters for keys are [a-z0-9.-_/].
     * @param c the character
     * @return whether character is allowed in a key
     */
    protected static boolean isValidKey(final char c) {
        return isValidNamespace(c) || c == '/';
    }

}
