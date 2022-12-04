package me.pesekjak.machine.world;

import me.pesekjak.machine.chunk.Chunk;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.blocks.WorldBlock;
import me.pesekjak.machine.world.dimensions.DimensionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static me.pesekjak.machine.chunk.Chunk.CHUNK_SIZE_BITS;

/**
 * Represents a world, which may contain entities, chunks and blocks.
 */
public interface World extends ServerProperty {

    /**
     * @return atomic reference of the manager
     */
    @ApiStatus.Internal
    @NotNull AtomicReference<WorldManager> getManagerReference();

    /**
     * @return manager of the world
     */
    default @Nullable WorldManager getManager() {
        return getManagerReference().get();
    }

    /**
     * @return name of the world
     */
    @NotNull NamespacedKey getName();

    /**
     * @return uuid of the world
     */
    @NotNull UUID getUuid();

    /**
     * @return dimension of the world
     */
    @NotNull DimensionType getDimensionType();

    /**
     * @return world type of the world
     */
    @NotNull WorldType getWorldType();

    /**
     * @return seed of the world
     */
    long getSeed();

    /**
     * @return difficulty of the world
     */
    @NotNull Difficulty getDifficulty();

    /**
     * @return location of the world spawn of the world
     */
    @NotNull Location getWorldSpawn();

    /**
     * @return if the world is loaded
     */
    boolean isLoaded();

    /**
     * @return set of all active entities in the world
     */
    @Unmodifiable @NotNull Set<Entity> getEntities();

    /**
     * Loads the world.
     */
    void load();

    /**
     * Unloads the world.
     */
    void unload();

    /**
     * Saves the world and its regions.
     */
    void save();

    /**
     * Loads player into the world.
     * @param player player to load
     */
    void loadPlayer(@NotNull Player player);

    /**
     * Unloads player from the world.
     * @param player player to unload
     */
    void unloadPlayer(@NotNull Player player);

    /**
     * Spawns an entity to the world at given location.
     * @param entity entity to spawn
     * @param location location to spawn
     * @return if the operation was successful
     */
    boolean spawn(@NotNull Entity entity, @NotNull Location location);

    /**
     * Removes an entity from the world.
     * @param entity entity to remove
     * @return if the operation was successful
     */
    boolean remove(@NotNull Entity entity);

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
     * Sets a world block at given position to a different block type.
     * @param blockType new block type
     * @param position position of the block
     * @param reason reason why the block type was set
     * @param replaceReason reason why the previous block type was removed
     * @param source source of the change
     */
    void setBlock(@NotNull BlockType blockType, @NotNull BlockPosition position, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source);

    /**
     * Sets a world block at given location to a different block type.
     * @param blockType new block type
     * @param location location of the block
     * @param reason reason why the block type was set
     * @param replaceReason reason why the previous block type was removed
     * @param source source of the change
     */
    default void setBlock(@NotNull BlockType blockType, @NotNull Location location, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source) {
        setBlock(blockType, location.toBlockPosition(), reason, replaceReason, source);
    }

    /**
     * Sets a world block at given position to a different block type.
     * @param blockType new block type
     * @param position position of the block
     */
    default void setBlock(@NotNull BlockType blockType, @NotNull BlockPosition position) {
        setBlock(blockType, position, BlockType.CreateReason.SET, BlockType.DestroyReason.REMOVED, null);
    }

    /**
     * Sets a world block at given location to a different block type.
     * @param blockType new block type
     * @param location location of the block
     */
    default void setBlock(@NotNull BlockType blockType, @NotNull Location location) {
        setBlock(blockType, location, BlockType.CreateReason.SET, BlockType.DestroyReason.REMOVED, null);
    }

    /**
     * Returns block from the world at given position,
     * if the part of the world has not been generated yet, it should be
     * and the generated block should be returned.
     * @param position position of the block
     * @return world block at given position
     */
    @NotNull WorldBlock getBlock(@NotNull BlockPosition position);

    /**
     * Returns block from the world at given position,
     * if the part of the world has not been generated yet, it should be
     * and the generated block should be returned.
     * @param location location of the block
     * @return world block at given location
     */
    default @NotNull WorldBlock getBlock(@NotNull Location location) {
        return getBlock(location.toBlockPosition());
    }

    // TODO Biomes support

    /**
     * Changes the world difficulty.
     * @param difficulty new difficulty
     */
    void setDifficulty(@NotNull Difficulty difficulty);

    /**
     * Changes the world spawn location of the world.
     * @param location new location
     */
    void setWorldSpawn(@NotNull Location location);

}
