package org.machinemc.server.world.generation;

import lombok.Getter;
import org.machinemc.api.world.World;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.generation.GeneratedSection;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.generation.Generator;

/**
 * Simple flat world stone generator.
 */
@Getter
public class FlatStoneGenerator implements Generator {

    private final Machine server;
    @Getter
    private final long seed;

    private final BlockType air;
    private final BlockType stone;

    private final Biome biome;

    public FlatStoneGenerator(Machine server, long seed) {
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
    public GeneratedSection populateChunk(int chunkX, int chunkZ, int sectionIndex, World world) {
        return new GeneratedSectionImpl(
                new BlockType[]{sectionIndex > 4 ? air : stone},
                new short[GeneratedSection.BLOCK_DATA_SIZE],
                new Biome[]{biome},
                new short[GeneratedSection.BLOCK_DATA_SIZE],
                new NBTCompound[GeneratedSection.BLOCK_DATA_SIZE]);
    }

}
