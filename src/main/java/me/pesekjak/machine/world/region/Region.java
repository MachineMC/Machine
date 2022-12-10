package me.pesekjak.machine.world.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.chunk.WorldChunk;
import me.pesekjak.machine.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.IOException;

/**
 * Represents a 32x32 grid of chunks.
 */
@RequiredArgsConstructor
@Getter
public abstract class Region {

    protected final @NotNull World world;
    protected final int x;
    protected final int z;
    protected final @Nullable WorldChunk[][] grid = new WorldChunk[32][32];

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
    public abstract @NotNull WorldChunk getChunk(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z);

    /**
     * Returns true if the chunk at given coordinates has not yet been generated - its
     * save doesn't contains any data about it.
     * @param x x coordinate of the chunk in the region
     * @param z z coordinate of the chunk in the region
     * @return true if chunk has not been generated
     */
    public abstract boolean shouldGenerate(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z);

}
