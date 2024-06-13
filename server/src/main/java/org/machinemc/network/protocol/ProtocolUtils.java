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
package org.machinemc.network.protocol;

import io.netty.buffer.ByteBuf;

/**
 * Utilities for working with the Minecraft protocol.
 */
public final class ProtocolUtils {

    private ProtocolUtils() {
        throw new UnsupportedOperationException();
    }

    public static final int VAR_SEGMENT_BITS = 0x7F;
    public static final int VAR_CONTINUE_BIT = 0x80;

    private static final int[] VAR_INT_LENGTHS = new int[33];

    static {
        for (int i = 0; i < 32; ++i) VAR_INT_LENGTHS[i] = (int) Math.ceil((31 - (i - 1)) / 7d);
        VAR_INT_LENGTHS[32] = 1;
    }

    /**
     * Returns the byte size of a number encoded as a variable integer.
     *
     * @param value value
     * @return byte size
     */
    public static int varIntLength(final int value) {
        return VAR_INT_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }

    /**
     * Returns whether the byte has continuation bit (for var-int decoding).
     *
     * @param b byte
     * @return whether the byte has continuation bit
     */
    public static boolean hasContinuationBit(final byte b) {
        return (b & VAR_CONTINUE_BIT) == VAR_CONTINUE_BIT;
    }

    /**
     * Reads next var int from the buffer.
     * <p>
     * For more information about var integers see
     * <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VarInt and VarLong</a>.
     *
     * @param buf buffer to read from
     * @return next var int
     */
    public static int readVarInt(final ByteBuf buf) {
        int value = 0;
        int position = 0;
        byte currentByte;
        while (true) {
            currentByte = buf.readByte();
            value |= (currentByte & VAR_SEGMENT_BITS) << position;
            if ((currentByte & VAR_CONTINUE_BIT) == 0) break;
            position += 7;
            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }
        return value;
    }

    /**
     * Writes var int to the buffer.
     * <p>
     * For more information about var integers see
     * <a href="https://wiki.vg/Protocol#VarInt_and_VarLong">VarInt and VarLong</a>.
     *
     * @param buf buffer to write in
     * @param value value to write
     */
    public static void writeVarInt(final ByteBuf buf, final int value) {
        int i = value;
        while (true) {
            if ((i & ~VAR_SEGMENT_BITS) == 0) {
                buf.writeByte((byte) i);
                return;
            }
            buf.writeByte((byte) ((i & VAR_SEGMENT_BITS) | VAR_CONTINUE_BIT));
            i >>>= 7;
        }
    }

}
