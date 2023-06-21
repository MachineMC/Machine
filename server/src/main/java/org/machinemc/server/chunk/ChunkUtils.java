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
package org.machinemc.server.chunk;

import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.world.BlockPosition;

import static org.machinemc.api.chunk.Chunk.CHUNK_SIZE_BITS;

/**
 * Utils for easier working with chunks.
 */
public final class ChunkUtils {

    /**
     * Magic number for MOTION_BLOCKING encoding.
     *
     * @see <a href="https://wiki.vg/Chunk_Format#MOTION_BLOCKING_encoding">Motion Blocking Encoding</a>
     */
    private static final int[] MAGIC = {
            -1, -1, 0, Integer.MIN_VALUE, 0, 0, 1431655765, 1431655765, 0, Integer.MIN_VALUE,
            0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756,
            0, Integer.MIN_VALUE, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0,
            390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378,
            306783378, 0, 286331153, 286331153, 0, Integer.MIN_VALUE, 0, 3, 252645135, 252645135,
            0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0,
            204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970,
            178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862,
            0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0,
            138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 126322567,
            126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197,
            0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0,
            104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893,
            97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282,
            0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0,
            84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431,
            79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303,
            0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0,
            70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, Integer.MIN_VALUE,
            0, 5};

    private ChunkUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts a global coordinate to a coordinate of
     * the section the it's in.
     * <p>
     * Example: 5 -> 0; -1 -> -1; 16 -> 1
     * @param xyz the coordinate to convert
     * @return the chunk X, Y or Z coordinate
     */
    public static int getChunkCoordinate(final int xyz) {
        return xyz >> CHUNK_SIZE_BITS;
    }

    /**
     * Converts a global coordinate to a section coordinate.
     * <p>
     * Example: 5 -> 5; -1 -> 15; 16 -> 0
     * @param xyz global coordinate
     * @return section coordinate
     */
    public static int getSectionRelativeCoordinate(final int xyz) {
        return xyz & 0xF;
    }

    /**
     * Creates a unique index for a block within a chunk from its chunk coordinates.
     * @param x x
     * @param y y
     * @param z z
     * @return an index which can be used to store and retrieve later data linked to a block position
     */
    public static int getBlockIndex(final int x, final int y, final int z) {
        if (x > 15 || x < 0 || z > 15 || z < 0 || y > 4096 || y < 0)
            throw new IllegalArgumentException("The block is out of chunk bounds");
        int index = 0;
        index |= y << 8;
        index |= z << 4;
        index |= x;
        return index;
    }

    /**
     * @param index an index computed from {@link #getBlockIndex(int, int, int)}
     * @param chunkX the chunk X
     * @param chunkZ the chunk Z
     * @return the position of the block
     */
    public static BlockPosition getBlockPosition(final int index, final int chunkX, final int chunkZ) {
        final int x = (index & 0xF) + Chunk.CHUNK_SIZE_X * chunkX;
        final int y = index >> 8;
        final int z = (index & 0xF0) + Chunk.CHUNK_SIZE_Z * chunkZ;
        return new BlockPosition(x, y, z);
    }

    /**
     * Encodes blocks for Minecraft's MOTION_BLOCKING map.
     * @param blocks blocks
     * @param bitsPerEntry bits per entry
     * @return encoded blocks
     */
    public static long[] encodeBlocks(final int[] blocks, final int bitsPerEntry) {
        final long maxEntryValue = (1L << bitsPerEntry) - 1;
        final int valuesPerLong = 64 / bitsPerEntry;
        final int magicIndex = 3 * (valuesPerLong - 1);
        final long divideMul = Integer.toUnsignedLong(MAGIC[magicIndex]);
        final long divideAdd = Integer.toUnsignedLong(MAGIC[magicIndex + 1]);
        final int divideShift = MAGIC[magicIndex + 2];
        final int size = (blocks.length + valuesPerLong - 1) / valuesPerLong;

        final long[] data = new long[size];

        for (int i = 0; i < blocks.length; i++) {
            final long value = blocks[i];
            final int cellIndex = (int) (i * divideMul + divideAdd >> 32L >> divideShift);
            final int bitIndex = (i - cellIndex * valuesPerLong) * bitsPerEntry;
            data[cellIndex] = data[cellIndex] & ~(maxEntryValue << bitIndex) | (value & maxEntryValue) << bitIndex;
        }

        return data;
    }

}
