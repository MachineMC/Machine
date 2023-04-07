package org.machinemc.server.world.generation;

import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.generation.GeneratedSection;
import org.machinemc.nbt.NBTCompound;

/**
 * Default implementation of the generated section.
 */
public record GeneratedSectionImpl(BlockType[] blockPalette,
                                   short[] blockData,
                                   Biome[] biomePalette,
                                   short[] biomeData,
                                   NBTCompound[] tileEntitiesData) implements GeneratedSection {

    @Override
    public BlockType[] getBlockPalette() {
        return blockPalette;
    }

    @Override
    public short[] getBlockData() {
        return blockData;
    }

    @Override
    public Biome[] getBiomePalette() {
        return biomePalette;
    }

    @Override
    public short[] getBiomeData() {
        return biomeData;
    }

    @Override
    public NBTCompound[] getTileEntitiesData() {
        return tileEntitiesData;
    }

}
