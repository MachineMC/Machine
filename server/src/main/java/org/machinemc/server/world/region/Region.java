package org.machinemc.server.world.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.world.World;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.IOException;

/**
 * Represents a 32x32 grid of chunks.
 */
@RequiredArgsConstructor
@Getter
public abstract class Region {

    protected final World world;
    protected final int x;
    protected final int z;
    protected final @Nullable Chunk[][] grid = new Chunk[32][32];

    /**
     * Saves the region.
     * @throws IOException if an I/O error occurs during saving
     */
    public abstract void save() throws IOException;

    /**
     * Returns chunk at given relative coordinates.
     * @param x x coordinate of the chunk in the region
     * @param z z coordinate of the chunk in the region
     * @return chunk at given coordinates
     */
    public abstract Chunk getChunk(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z);

    /**
     * Returns true if the chunk at given coordinates has not yet been generated - its
     * save doesn't contains any data about it.
     * @param x x coordinate of the chunk in the region
     * @param z z coordinate of the chunk in the region
     * @return true if chunk has not been generated
     */
    public abstract boolean shouldGenerate(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z);

}
