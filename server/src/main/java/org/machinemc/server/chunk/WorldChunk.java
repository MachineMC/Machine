package org.machinemc.server.chunk;

import lombok.Getter;
import org.machinemc.server.Server;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.world.World;

/**
 * Default implementation of the chunk.
 */
@Getter
public abstract class WorldChunk implements Chunk {

    protected final Server server;
    protected final World world;

    protected final int chunkX, chunkZ;
    protected final int minSection, maxSection;

    protected volatile boolean loaded = true;

    public WorldChunk(World world, int chunkX, int chunkZ) {
        this.server = world.getServer();
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.minSection = world.getDimensionType().getMinY() / Chunk.CHUNK_SECTION_SIZE;
        this.maxSection = (world.getDimensionType().getMinY() + world.getDimensionType().getHeight()) / Chunk.CHUNK_SECTION_SIZE;
    }

}
