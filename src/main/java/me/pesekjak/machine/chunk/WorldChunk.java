package me.pesekjak.machine.chunk;

import lombok.Getter;
import me.pesekjak.machine.Server;
import me.pesekjak.machine.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Default implementation of the chunk.
 */
@Getter
public abstract class WorldChunk implements Chunk {

    protected final @NotNull Server server;
    protected final @NotNull World world;

    protected final int chunkX, chunkZ;
    protected final int minSection, maxSection;

    protected volatile boolean loaded = true;

    public WorldChunk(@NotNull World world, int chunkX, int chunkZ) {
        this.server = world.getServer();
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.minSection = world.getDimensionType().getMinY() / Chunk.CHUNK_SECTION_SIZE;
        this.maxSection = (world.getDimensionType().getMinY() + world.getDimensionType().getHeight()) / Chunk.CHUNK_SECTION_SIZE;
    }

}
