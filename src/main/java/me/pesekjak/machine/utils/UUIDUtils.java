package me.pesekjak.machine.utils;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDUtils {

    private static final Pattern UUID_PATTERN = Pattern.compile("^\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}$");

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

    @Nullable
    public static UUID uuidFromString(@Nullable String string) {
        if (string == null)
            return null;
        if (!UUID_PATTERN.matcher(string).matches())
            return null;
        return UUID.fromString(string);
    }

}
