package me.pesekjak.machine.utils;

import java.util.UUID;

public class Utils {

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

}
