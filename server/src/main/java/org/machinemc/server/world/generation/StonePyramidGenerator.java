package org.machinemc.server.world.generation;

import lombok.Getter;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.World;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.generation.GeneratedSection;
import org.machinemc.api.world.generation.Generator;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.server.Server;

public class StonePyramidGenerator implements Generator {

    private final Machine server;
    @Getter
    private final long seed;

    private final BlockType air;
    private final BlockType stone;

    private final Biome biome;

    public StonePyramidGenerator(Machine server, long seed) {
        this.server = server;
        this.seed = seed;
        final BlockManager manager = server.getBlockManager();
        final BlockType air = manager.getBlockType(NamespacedKey.minecraft("air"));
        final BlockType stone = manager.getBlockType(NamespacedKey.minecraft("stone"));
        if(air == null || stone == null) throw new IllegalStateException();
        this.air = air;
        this.stone = stone;
        Biome biome = server.getBiome(NamespacedKey.minecraft("plains"));
        if(biome == null)
            biome = server.getBiomeManager().getBiomes().stream().iterator().next();
        if(biome == null) throw new IllegalStateException();
        this.biome = biome;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public GeneratedSection populateChunk(int chunkX, int chunkZ, int sectionIndex, World world) {
        if(sectionIndex < 4) {
            return new GeneratedSectionImpl(
                    new BlockType[]{stone},
                    new short[GeneratedSection.BLOCK_DATA_SIZE],
                    new Biome[]{biome},
                    new short[GeneratedSection.BLOCK_DATA_SIZE],
                    new NBTCompound[GeneratedSection.BLOCK_DATA_SIZE]);
        }
        if(sectionIndex > 4) {
            return new GeneratedSectionImpl(
                    new BlockType[]{air},
                    new short[GeneratedSection.BLOCK_DATA_SIZE],
                    new Biome[]{biome},
                    new short[GeneratedSection.BLOCK_DATA_SIZE],
                    new NBTCompound[GeneratedSection.BLOCK_DATA_SIZE]);
        }
        final BlockType[] palette = new BlockType[]{air, stone};
        final short[] data = new short[GeneratedSection.BLOCK_DATA_SIZE];

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final int h = Math.abs(x-8) + Math.abs(z-8) - 1;
                for (int y = 0; y < 16; y++) {
                    data[GeneratedSection.index(x, y, z)] = (short) (y < h ? 1 : 0);
                }
            }
        }

        return new GeneratedSectionImpl(palette, data,
                new Biome[]{biome},
                new short[GeneratedSection.BLOCK_DATA_SIZE],
                new NBTCompound[GeneratedSection.BLOCK_DATA_SIZE]);
    }

}
