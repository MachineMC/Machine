package me.pesekjak.machine.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDUtils {

    private static final Pattern UUID_PATTERN = Pattern.compile("^(\\p{XDigit}{8})-?(\\p{XDigit}{4})-?(\\p{XDigit}{4})-?(\\p{XDigit}{4})-?(\\p{XDigit}{12})$");
    private static final String DASHES_UUID_REPLACE = "$1-$2-$3-$4-$5";

    public static UUID uuidFromIntArray(int[] ints) {
        return new UUID((long) ints[0] << 32 | (long) ints[1] & 4294967295L, (long) ints[2] << 32 | (long) ints[3] & 4294967295L);
    }

    public static int[] uuidToIntArray(UUID uuid) {
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        return leastMostToIntArray(most, least);
    }

    private static int[] leastMostToIntArray(long most, long least) {
        return new int[]{(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }

    /**
     * parses a uuid string to a classic uuid
     * @param string the string uuid
     * @return parsed uuid
     */
    @Nullable
    @Contract(value = "null -> null", pure = true)
    public static UUID parseUUID(@Nullable String string) {
        if (string == null)
            return null;
        Matcher matcher = UUID_PATTERN.matcher(string);
        if (!matcher.matches())
            return null;
        return UUID.fromString(matcher.replaceFirst(DASHES_UUID_REPLACE));
    }

}
