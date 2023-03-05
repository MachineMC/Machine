package org.machinemc.server.world.generation;

import lombok.Getter;
import org.machinemc.server.Machine;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.BlockPosition;
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
    public BlockType generate(BlockPosition position) {
        if(position.getY() > 1) return air;
        return stone;
    }

}
