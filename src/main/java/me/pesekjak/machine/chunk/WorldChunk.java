package me.pesekjak.machine.chunk;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.biomes.Biome;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.blocks.BlockVisual;
import me.pesekjak.machine.world.blocks.WorldBlockImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Chunk of a world
 */
@Getter
public abstract class WorldChunk implements Chunk {

    protected final Machine server;
    protected final World world;

    protected final int chunkX, chunkZ;
    protected final int minSection, maxSection;

    protected volatile boolean loaded = true;

    public WorldChunk(World world, int chunkX, int chunkZ) {
        this.server = (Machine) world.getServer(); // TODO cleanup once the api is done
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.minSection = world.getDimensionType().getMinY() / Chunk.CHUNK_SECTION_SIZE;
        this.maxSection = (world.getDimensionType().getMinY() + world.getDimensionType().getHeight()) / Chunk.CHUNK_SECTION_SIZE;
    }

}
