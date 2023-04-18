package org.machinemc.server.utils;

import com.google.common.base.Preconditions;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class related to operations with uuids.
 */
public final class UUIDUtils {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^(\\p{XDigit}{8})-?"
            + "(\\p{XDigit}{4})-?"
            + "(\\p{XDigit}{4})-?"
            + "(\\p{XDigit}{4})-?"
            + "(\\p{XDigit}{12})$");
    private static final @RegExp String DASHES_UUID_REPLACE = "$1-$2-$3-$4-$5";

    private UUIDUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts an int array into a UUID.
     * @param ints The ints (must be an array of 4 integers)
     * @return The UUID
     */
    public static UUID uuidFromIntArray(final int[] ints) {
        Preconditions.checkArgument(ints.length == 4, "The length of ints must be 4");
        return new UUID(
                (long) ints[0] << 32 | (long) ints[1] & 4294967295L,
                (long) ints[2] << 32 | (long) ints[3] & 4294967295L
        );
    }

    /**
     * Converts a UUID into an int array of 4 integers.
     * @param uuid The UUID
     * @return The int array
     */
    public static int[] uuidToIntArray(final UUID uuid) {
        final long most = uuid.getMostSignificantBits();
        final long least = uuid.getLeastSignificantBits();
        return leastMostToIntArray(most, least);
    }

    /**
     * Converts the most and least bits of a UUID into an int array.
     * @param most The most bits of a UUID
     * @param least The least bits of a UUID
     * @return The int array
     */
    private static int[] leastMostToIntArray(final long most, final long least) {
        return new int[]{(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }

    /**
     * parses a uuid string to a classic uuid.
     * @param string the string uuid
     * @return parsed uuid
     */
    public static @Nullable UUID parseUUID(final @Nullable String string) {
        if (string == null)
            return null;
        final Matcher matcher = UUID_PATTERN.matcher(string);
        if (!matcher.matches())
            return null;
        return UUID.fromString(matcher.replaceFirst(DASHES_UUID_REPLACE));
    }

}
