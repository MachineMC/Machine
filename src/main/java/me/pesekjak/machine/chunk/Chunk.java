package me.pesekjak.machine.chunk;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.biomes.Biome;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.blocks.WorldBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class Chunk {

    public static final int CHUNK_SIZE_X = 16,
                            CHUNK_SIZE_Z = 16,
                            CHUNK_SECTION_SIZE = 16;

    protected final Machine server;
    protected final Instance instance;
    protected final World world;

    protected final UUID uuid;

    protected final int chunkX, chunkZ;
    protected final int minSection, maxSection;

    protected volatile boolean loaded = true;

    public Chunk(Instance instance, int chunkX, int chunkZ) {
        this.server = instance.getServer();
        this.instance = instance;
        this.world = instance.getWorld();
        this.uuid = UUID.randomUUID();
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.minSection = world.getDimensionType().getMinY() / CHUNK_SECTION_SIZE;
        this.maxSection = (world.getDimensionType().getMinY() + world.getDimensionType().getHeight()) / CHUNK_SECTION_SIZE;
    }

    public abstract WorldBlock getBlock(int x, int y, int z);

    public abstract void setBlock(int x, int y, int z, @NotNull BlockType blockType);

    public abstract Biome getBiome(int x, int y, int z);

    public abstract void setBiome(int x, int y, int z, @NotNull Biome biome);

    public abstract @NotNull List<Section> getSections();

    public abstract @NotNull Section getSection(int section);

    public @NotNull Section getSectionAt(int blockY) {
        return getSection(ChunkUtils.getChunkCoordinate(blockY));
    }

    public abstract void sendChunk(@NotNull Player player);

    public abstract void unloadChunk(@NotNull Player player);

    public abstract @NotNull Chunk copy(@NotNull Instance instance, int chunkX, int chunkZ);

    public abstract void reset();

}
