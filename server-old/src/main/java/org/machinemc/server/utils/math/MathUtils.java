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
package org.machinemc.server.utils.math;

/**
 * Utility class for math operations.
 */
public final class MathUtils {

    private MathUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param n number to check
     * @return by how many bits the number can be represented
     */
    public static int bitsToRepresent(final int n) {
        if (n < 1) throw new IllegalStateException("Number must be greater than 0");
        return Integer.SIZE - Integer.numberOfLeadingZeros(n);
    }

}
