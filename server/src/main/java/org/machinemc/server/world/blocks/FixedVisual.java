package org.machinemc.server.world.blocks;

import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.blocks.BlockVisual;

/**
 * Fixed visual providing always the same block data.
 */
public record FixedVisual(BlockData blockData) implements BlockVisual {

    @Override
    public BlockData getBlockData() {
        return blockData.clone();
    }

}
