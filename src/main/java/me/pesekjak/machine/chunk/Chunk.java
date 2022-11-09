package me.pesekjak.machine.chunk;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.biomes.Biome;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.blocks.BlockVisual;
import me.pesekjak.machine.world.blocks.WorldBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Chunk of a world
 */
@Getter
public abstract class Chunk {

    public static final int CHUNK_SIZE_X = 16,
                            CHUNK_SIZE_Z = 16,
                            CHUNK_SECTION_SIZE = 16;

    protected final Machine server;
    protected final World world;

    protected final UUID uuid;

    protected final int chunkX, chunkZ;
    protected final int minSection, maxSection;

    protected volatile boolean loaded = true;

    public Chunk(World world, int chunkX, int chunkZ) {
        this.server = world.getServer();
        this.world = world;
        this.uuid = UUID.randomUUID();
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.minSection = world.getDimensionType().getMinY() / CHUNK_SECTION_SIZE;
        this.maxSection = (world.getDimensionType().getMinY() + world.getDimensionType().getHeight()) / CHUNK_SECTION_SIZE;
    }

    /**
     * Returns a world block at given location in this chunk
     * @param x x coordinate of the block in this chunk
     * @param y y coordinate of the block in this chunk
     * @param z z coordinate of the block in this chunk
     * @return world block at given location
     */
    public abstract WorldBlock getBlock(int x, int y, int z);

    /**
     * Sets a new block type for a world block at given location in this chunk
     * @param x x coordinate of the block in this chunk
     * @param y y coordinate of the block in this chunk
     * @param z z coordinate of the block in this chunk
     * @param blockType new block type
     * @param reason reason of the change
     * @param replaceReason reason of the replacement of the old block type
     * @param source source of the change
     * @return world block that has been changed
     */
    public abstract WorldBlock setBlock(int x, int y, int z, @NotNull BlockType blockType, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source);

    /**
     * Changes the visual of a block at given location in this chunk
     * @param x x coordinate of the block in this chunk
     * @param y y coordinate of the block in this chunk
     * @param z z coordinate of the block in this chunk
     * @param visual new visual for the world block
     */
    public abstract void setVisual(int x, int y, int z, @NotNull BlockVisual visual);

    /**
     * Returns a biome at given location in this chunk
     * @param x x coordinate of the biome
     * @param y y coordinate of the biome
     * @param z z coordinate of the biome
     * @return biome at given location
     */
    public abstract Biome getBiome(int x, int y, int z);

    /**
     * Sets a new biome at the given location.
     * @param x x coordinate of the biome
     * @param y y coordinate of the biome
     * @param z z coordinate of the biome
     * @param biome new biome
     */
    public abstract void setBiome(int x, int y, int z, @NotNull Biome biome);

    /**
     * @return all sections of this chunk
     */
    public abstract @NotNull List<Section> getSections();

    /**
     * @param section index of the section
     * @return section with given index
     */
    public abstract @NotNull Section getSection(int section);

    /**
     * @param blockY y coordinate of the section
     * @return section at given y coordinate
     */
    public @NotNull Section getSectionAt(int blockY) {
        return getSection(ChunkUtils.getChunkCoordinate(blockY));
    }

    /**
     * Sends a chunk to a player.
     * @param player player to send chunk for
     */
    public abstract void sendChunk(@NotNull Player player);

    /**
     * Unloads the chunk for the player.
     * @param player player to unload chunk for
     */
    public abstract void unloadChunk(@NotNull Player player);

    /**
     * Creates a copy of this chunk in a given world at given coordinates.
     * @param world world to create the chunk for
     * @param chunkX x coordinate of the copied chunk
     * @param chunkZ z coordinate of the copied chunk
     * @return copy of this chunk
     */
    public abstract @NotNull Chunk copy(@NotNull World world, int chunkX, int chunkZ);

    /**
     * Resets the chunk and its data.
     */
    public abstract void reset();

    /**
     * @return x-coordinate of region the chunk is in
     */
    public int getRegionX() {
        return ChunkUtils.getRegionCoordinate(chunkX);
    }

    /**
     * @return z-coordinate of region the chunk is in
     */
    public int getRegionZ() {
        return ChunkUtils.getRegionCoordinate(chunkZ);
    }

}
