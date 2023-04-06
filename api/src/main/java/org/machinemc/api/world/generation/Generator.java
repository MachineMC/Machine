package org.machinemc.api.world.generation;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.world.World;

/**
 * Represents generator of a world.
 */
// TODO Biome support
public interface Generator extends ServerProperty {

    /**
     * Generates entries for a section in a chunk.
     * @param chunkX x coordinate of the chunk
     * @param chunkZ z coordinate of the chunk
     * @param sectionIndex index of the section
     * @param world world of the chunk
     * @return content for the chunk section
     */
    GeneratedSection populateChunk(final int chunkX, final int chunkZ, final int sectionIndex, World world);

    /**
     * @return seed used by this generator
     */
    long getSeed();

}
