package org.machinemc.api.chunk;

import org.jetbrains.annotations.Async;
import org.machinemc.api.entities.Player;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.world.World;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.Future;

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
     * @param x x coordinate of the block in this chunk
     * @param y y coordinate of the block in this chunk
     * @param z z coordinate of the block in this chunk
     * @return world block at given location
     */
    WorldBlock getBlock(int x, int y, int z);

    @Async.Execute
    Future<WorldBlock> getBlockAsync(int x, int y, int z);

    /**
     * Sets a new block type for a world block at given location in this chunk.
     * @param x x coordinate of the block in this chunk
     * @param y y coordinate of the block in this chunk
     * @param z z coordinate of the block in this chunk
     */
    void setBlock(int x, int y, int z, BlockType blockType);

    /**
     * Returns a biome at given location in this chunk.
     * @param x x coordinate of the biome
     * @param y y coordinate of the biome
     * @param z z coordinate of the biome
     * @return biome at given location
     */
    Biome getBiome(int x, int y, int z);

    /**
     * Sets a new biome at the given location.
     * @param x x coordinate of the biome
     * @param y y coordinate of the biome
     * @param z z coordinate of the biome
     * @param biome new biome
     */
    void setBiome(int x, int y, int z, Biome biome);

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
