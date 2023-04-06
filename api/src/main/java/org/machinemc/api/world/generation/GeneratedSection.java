package org.machinemc.api.world.generation;

import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.nbt.NBTCompound;

/**
 * Represents a section with content generated by a world generator.
 */
public interface GeneratedSection {

    int BLOCK_DATA_SIZE = 16*16*16;
    int BIOME_DATA_SIZE = 4*4*4;

    /**
     * @return palette of this section generated by the chunk
     */
    BlockType[] getBlockPalette();

    /**
     * Block data of this section, values need to match the palette indices of
     * block types.
     * <p>
     * For index encoding and decoding use {@link GeneratedSection#index(int, int, int)} and {@link GeneratedSection#decode(int)}.
     * <p>
     * For specifying data of individual generated blocks {@link GeneratedSection#getTileEntitiesData()} is used.
     * @return block data
     * @apiNote size has to be {@link GeneratedSection#BLOCK_DATA_SIZE}
     */
    short[] getBlockData();

    /**
     * @return biome palette of this section generated by the chunk
     */
    Biome[] getBiomePalette();

    /**
     * Biome data of this section, values need to match the palette indices of
     * biomes.
     * <p>
     * For index encoding and decoding use {@link GeneratedSection#index(int, int, int)} and {@link GeneratedSection#decode(int)}.
     * @return biome data
     * @apiNote size has to be {@link GeneratedSection#BIOME_DATA_SIZE}
     */
    short[] getBiomeData();

    /**
     * Used for generating of tile entities, indices of the array need to match encoding positions
     * using {@link GeneratedSection#index(int, int, int)}.
     * <p>
     * NBT Compounds provided by will merge to initialized compounds of the blocks. Block type
     * mapped to the same position has to be a tile entity {@link org.machinemc.api.world.blocks.EntityBlockType}
     * @return nbt of tile entities generated in the section
     * @apiNote size has to be {@link GeneratedSection#BLOCK_DATA_SIZE}
     */
    NBTCompound[] getTileEntitiesData();

    static int index(final int x, final int y, final int z) {
        return (x & 0xF) | ((y << 4) & 0xF0) | (z << 8);
    }

    static int[] decode(final int index) {
        final int[] values = new int[3];
        values[0] = index & 0xF;
        values[1] = index & 0xF0;
        values[2] = index & 0xF00;
        return values;
    }

}
