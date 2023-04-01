package org.machinemc.server.world.generation;

import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.generation.Generator;
import org.machinemc.nbt.NBTCompound;

public record SectionContentImpl(BlockType[] palette,
                                 short[] data,
                                 NBTCompound[] tileEntitiesData) implements Generator.SectionContent {

    @Override
    public BlockType[] getPalette() {
        return palette;
    }

    @Override
    public short[] getData() {
        return data;
    }

    @Override
    public NBTCompound[] getTileEntitiesData() {
        return tileEntitiesData;
    }

}
