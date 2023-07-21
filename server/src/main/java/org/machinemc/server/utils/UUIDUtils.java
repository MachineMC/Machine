/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.utils;

import com.google.common.base.Preconditions;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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
    public static Optional<UUID> parseUUID(final @Nullable String string) {
        if (string == null)
            return Optional.empty();
        final Matcher matcher = UUID_PATTERN.matcher(string);
        if (!matcher.matches())
            return Optional.empty();
        return Optional.of(UUID.fromString(matcher.replaceFirst(DASHES_UUID_REPLACE)));
    }

}
