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
package org.machinemc.api.world.generation;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.world.World;

/**
 * Represents generator of a world.
 */
public interface Generator extends ServerProperty {

    /**
     * Generates entries for a section in a chunk.
     * @param chunkX x coordinate of the chunk
     * @param chunkZ z coordinate of the chunk
     * @param sectionIndex index of the section
     * @param world world of the chunk
     * @return content for the chunk section
     */
    GeneratedSection populateChunk(int chunkX, int chunkZ, int sectionIndex, World world);

    /**
     * @return seed used by this generator
     */
    long getSeed();

}
