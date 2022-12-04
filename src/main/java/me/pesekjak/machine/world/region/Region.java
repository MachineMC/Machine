package me.pesekjak.machine.world.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.chunk.WorldChunk;
import me.pesekjak.machine.world.WorldImpl;
import org.jetbrains.annotations.Range;

/**
 * Represents a 32x32 grid of chunks.
 */
@RequiredArgsConstructor
@Getter
public abstract class Region {

    protected final WorldImpl world;
    protected final int x;
    protected final int z;
    protected final WorldChunk[][] grid = new WorldChunk[32][32];

    /**
     * Saves the region.
     */
    public abstract void save();

    /**
     * Returns chunk at given relative coordinates.
     * @param x x coordinate of the chunk in the region
     * @param z z coordinate of the chunk in the region
     * @return chunk at given coordinates
     */
    public abstract WorldChunk getChunk(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z);

    /**
     * Returns true if the chunk at given coordinates has not yet been generated - its
     * save doesn't contains any data about it.
     * @param x x coordinate of the chunk in the region
     * @param z z coordinate of the chunk in the region
     * @return true if chunk has not been generated
     */
    public abstract boolean shouldGenerate(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z);

}
