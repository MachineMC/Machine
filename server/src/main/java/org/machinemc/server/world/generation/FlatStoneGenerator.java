package org.machinemc.server.world.generation;

import lombok.Getter;
import org.machinemc.api.world.World;
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
    private final long seed;

    private final BlockType air;
    private final BlockType stone;

    public FlatStoneGenerator(Machine server, long seed) {
        this.server = server;
        this.seed = seed;
        final BlockManager manager = server.getBlockManager();
        final BlockType air = manager.getBlockType(NamespacedKey.minecraft("air"));
        final BlockType stone = manager.getBlockType(NamespacedKey.minecraft("stone"));
        if(air == null || stone == null) throw new IllegalStateException();
        this.air = air;
        this.stone = stone;
    }

    @Override
    public SectionContent populateChunk(int chunkX, int chunkZ, int sectionIndex, World world) {
        return new SectionContent() {

            final BlockType[] palette = new BlockType[]{sectionIndex > 4 ? air : stone};

            @Override
            public BlockType[] getPalette() {
                return palette;
            }

            @Override
            public short[] getData() {
                return new short[SectionContent.DATA_SIZE];
            }

            @Override
            public NBTCompound[] getTileEntitiesData() {
                return new NBTCompound[SectionContent.DATA_SIZE];
            }
        };
    }

}
