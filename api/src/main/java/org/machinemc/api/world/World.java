package org.machinemc.api.world;

import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.Player;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.api.world.blocks.WorldBlockManager;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.api.world.generation.Generator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.machinemc.api.chunk.Chunk.CHUNK_SIZE_BITS;

/**
 * Represents a world, which may contain entities, chunks and blocks.
 */
public interface World extends ServerProperty {

    /**
     * @return atomic reference of the manager
     */
    @ApiStatus.Internal
    AtomicReference<WorldManager> getManagerReference();

    /**
     * @return manager of the world
     */
    default @Nullable WorldManager getManager() {
        return getManagerReference().get();
    }

    /**
     * @return name of the world
     */
    NamespacedKey getName();

    /**
     * @return uuid of the world
     */
    UUID getUuid();

    /**
     * @return dimension of the world
     */
    DimensionType getDimensionType();

    /**
     * @return world type of the world
     */
    WorldType getWorldType();

    /**
     * @return seed of the world
     */
    long getSeed();

    /**
     * @return difficulty of the world
     */
    Difficulty getDifficulty();

    /**
     * @return location of the world spawn of the world
     */
    Location getWorldSpawn();

    /**
     * @return if the world is loaded
     */
    boolean isLoaded();

    /**
     * @return set of all active entities in the world
     */
    @Unmodifiable Set<Entity> getEntities();

    /**
     * @return generator used by the world
     */
     Generator getGenerator();

    /**
     * @return world block manager of this world
     */
    WorldBlockManager getWorldBlockManager();

    /**
     * Loads the world.
     * @throws IOException if an I/O error occurs during loading
     */
    void load() throws IOException;

    /**
     * Unloads the world.
     * @throws IOException if an I/O error occurs during unloading
     */
    void unload() throws IOException;

    /**
     * Saves the world and its regions.
     * @throws IOException if an I/O error occurs during saving
     */
    void save() throws IOException;

    /**
     * Loads player into the world.
     * @param player player to load
     */
    void loadPlayer(Player player);

    /**
     * Unloads player from the world.
     * @param player player to unload
     */
    void unloadPlayer(Player player);

    /**
     * Spawns an entity to the world at given location.
     * @param entity entity to spawn
     * @param location location to spawn
     * @return if the operation was successful
     */
    boolean spawn(Entity entity, Location location);

    /**
     * Removes an entity from the world.
     * @param entity entity to remove
     * @return if the operation was successful
     */
    boolean remove(Entity entity);

    /**
     * Returns chunk with given chunk coordinates
     * @param chunkX x coordinate of the chunk
     * @param chunkZ z coordinate of the chunk
     * @return chunk with given coordinates
     */
    Chunk getChunk(int chunkX, int chunkZ);

    /**
     * @param position position
     * @return chunk at given position
     */
    default Chunk getChunk(BlockPosition position) {
        return getChunk(position.getX() >> CHUNK_SIZE_BITS, position.getZ() >> CHUNK_SIZE_BITS);
    }

    /**
     * @param location location
     * @return chunk at given location
     */
    default Chunk getChunk(Location location) {
        return getChunk(location.toBlockPosition());
    }

    /**
     * Returns block from the world at given position,
     * if the part of the world has not been generated yet, it should be
     * and the generated block should be returned.
     * @param position position of the block
     * @return world block at given position
     */
    WorldBlock getBlock(BlockPosition position);

    /**
     * Returns block from the world at given position,
     * if the part of the world has not been generated yet, it should be
     * and the generated block should be returned.
     * @param location location of the block
     * @return world block at given location
     */
    default WorldBlock getBlock(Location location) {
        return getBlock(location.toBlockPosition());
    }

    /**
     * Returns block async from the world at given position,
     * if the part of the world has not been generated yet, it should be
     * and the generated block should be returned.
     * @param position position of the block
     * @return world block at given position
     */
    Future<WorldBlock> getBlockAsync(BlockPosition position);

    /**
     * Returns block async from the world at given position,
     * if the part of the world has not been generated yet, it should be
     * and the generated block should be returned.
     * @param location location of the block
     * @return world block at given position
     */
    default Future<WorldBlock> getBlockAsync(Location location) {
        return getBlockAsync(location.toBlockPosition());
    }

    /**
     * Sets a world block at given position to a different block type.
     * @param blockType new block type
     * @param position position of the block
     */
    void setBlock(BlockType blockType, BlockPosition position);

    /**
     * Sets a world block at given position to a different block type.
     * @param blockType new block type
     * @param location location of the block
     */
    default void setBlock(BlockType blockType, Location location) {
        setBlock(blockType, location.toBlockPosition());
    }

    /**
     * Sets new biome at given location in the world, keep in mind
     * the biome grid is 4x4.
     * @param biome new biome
     * @param position position
     */
    void setBiome(Biome biome, BlockPosition position);

    /**
     * Sets new biome at given location in the world, keep in mind
     * the biome grid is 4x4.
     * @param biome new biome
     * @param location location
     */
    default void setBiome(Biome biome, Location location) {
        setBiome(biome, location.toBlockPosition());
    }

    /**
     * Gets biome at given location in the world, keep in mind
     * the biome grid is 4x4.
     * <p>
     * If the part of the world has not been generated yet, it should be
     * and the generated biome should be returned.
     * @param position position
     * @return biome at given position
     */
    Biome getBiome(BlockPosition position);

    /**
     * Gets biome at given location in the world, keep in mind
     * the biome grid is 4x4.
     * <p>
     * If the part of the world has not been generated yet, it should be
     * and the generated biome should be returned.
     * @param location location
     * @return biome at given location
     */
    default Biome getBiome(Location location) {
        return getBiome(location.toBlockPosition());
    }

    /**
     * Changes the world difficulty.
     * @param difficulty new difficulty
     */
    void setDifficulty(Difficulty difficulty);

    /**
     * Changes the world spawn location of the world.
     * @param location new location
     */
    void setWorldSpawn(Location location);

}
