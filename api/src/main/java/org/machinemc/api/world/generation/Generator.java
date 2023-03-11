package org.machinemc.api.world.generation;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.Pair;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockType;

/**
 * Represents generator of a world.
 */
public interface Generator extends ServerProperty {

    int DATA_SIZE = 16*16*16;

    /**
     * Generates entries for a section in a chunk.
     * @param chunkX x coordinate of the chunk
     * @param chunkZ z coordinate of the chunk
     * @param sectionIndex index of the section
     * @param world world of the chunk
     * @return pair of palette of used block types and block data,
     * for position encoding see {@link Generator#index(int, int, int)} and
     * for position decoding see {@link Generator#decode(int)}
     */
    Pair<BlockType[], short[]> populateChunk(final int chunkX, final int chunkZ, final int sectionIndex, World world);

    static int index(final int x, final int y, final int z) {
        return (x & 0xF) | ((y << 4) & 0xF0) | (z << 8);
    }

    static int[] decode(final int index) {
        final int[] values = new int[3];
        values[0] = index & 0xF;
        values[1] = index & 0xF0;
        values[2] = index & 0xF00;
        return values;
    }

}
