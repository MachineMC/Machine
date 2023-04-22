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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.chunk;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.machinemc.api.entities.Player;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.world.World;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.nbt.NBTCompound;

import java.util.List;

/**
 * Represents a chunk (16x16 area) in a world.
 */
public interface Chunk extends ServerProperty {

    int CHUNK_SIZE_X = 16,
            CHUNK_SIZE_Z = 16,
            CHUNK_SECTION_SIZE = 16;

    int CHUNK_SIZE_BITS = 4;

    /**
     * @return world the chunk is in
     */
    World getWorld();

    /**
     * @return x coordinate of the chunk
     */
    int getChunkX();

    /**
     * @return z coordinate of the chunk
     */
    int getChunkZ();

    /**
     * @return index of the bottom section
     */
    int getMinSection();

    /**
     * @return index of the top section
     */
    int getMaxSection();

    /**
     * @return if the chunk is loaded
     */
    boolean isLoaded();

    /**
     * Returns a world block at given location in this chunk.
     * @param x x
     * @param y y
     * @param z z
     * @return world block at given location
     */
    WorldBlock getBlock(@Range(from = 0, to = 15) int x, int y, @Range(from = 0, to = 15) int z);

    /**
     * Sets a new block type for a world block at given location in this chunk.
     * @param x x
     * @param y y
     * @param z z
     * @param blockType new block type
     */
    void setBlock(@Range(from = 0, to = 15) int x, int y, @Range(from = 0, to = 15) int z, BlockType blockType);

    /**
     * Returns clone of nbt of block at given location in this chunk.
     * @param x x
     * @param y y
     * @param z z
     * @return nbt of block at given location
     */
    NBTCompound getBlockNBT(@Range(from = 0, to = 15) int x, int y, @Range(from = 0, to = 15) int z);

    /**
     * Merges provided nbt compound to the compound of block at given coordinates.
     * @param x x
     * @param y y
     * @param z z
     * @param compound compound to merge
     */
    void mergeBlockNBT(@Range(from = 0, to = 15) int x, int y, @Range(from = 0, to = 15) int z, NBTCompound compound);

    /**
     * Sets new nbt to the block at given location in this chunk.
     * @param x x
     * @param y y
     * @param z z
     * @param compound new nbt
     */
    void setBlockNBT(@Range(from = 0, to = 15) int x,
                     int y,
                     @Range(from = 0, to = 15) int z,
                     @Nullable NBTCompound compound);

    /**
     * Returns a biome at given location in this chunk.
     * @param x x
     * @param y y
     * @param z z
     * @return biome at given location
     */
    Biome getBiome(@Range(from = 0, to = 15) int x, int y, @Range(from = 0, to = 15) int z);

    /**
     * Sets a new biome at the given location in this chunk.
     * @param x x
     * @param y y
     * @param z z
     * @param biome new biome
     */
    void setBiome(@Range(from = 0, to = 15) int x, int y, @Range(from = 0, to = 15) int z, Biome biome);

    /**
     * Returns unmodifiable list of all sections.
     * @return all sections of this chunk
     */
    @Unmodifiable List<Section> getSections();

    /**
     * Returns section with given index.
     * @param index index of the section
     * @return section with given index
     */
    Section getSection(int index);

    /**
     * Returns section at given y coordinate in the world.
     * @param blockY y coordinate of the section
     * @return section at given y coordinate
     */
    default Section getSectionAt(int blockY) {
        return getSection(blockY >> CHUNK_SIZE_BITS);
    }

    /**
     * Sends a chunk to a player.
     * @param player player to send chunk for
     */
    void sendChunk(Player player);

    /**
     * Unloads the chunk for the player.
     * @param player player to unload chunk for
     */
    void unloadChunk(Player player);

    /**
     * Resets the chunk and its data.
     */
    void reset();

}
