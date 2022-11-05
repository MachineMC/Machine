package me.pesekjak.machine.world.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.chunk.Chunk;
import me.pesekjak.machine.world.World;
import org.jetbrains.annotations.Range;

@RequiredArgsConstructor
@Getter
public abstract class Region {

    protected final World world;
    protected final int x;
    protected final int z;
    protected final Chunk[][] grid = new Chunk[32][32];

    public abstract void save();

    public abstract Chunk getChunk(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z);

    public abstract boolean shouldGenerate(@Range(from = 0, to = 31) int x, @Range(from = 0, to = 31) int z);

}
