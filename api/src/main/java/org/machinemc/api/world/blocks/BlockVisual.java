package org.machinemc.api.world.blocks;

import org.machinemc.api.world.BlockData;
import org.jetbrains.annotations.Contract;

/**
 * Represents visual of a world block.
 */
public interface BlockVisual {

    /**
     * @return clone of the block data currently used by the visual
     */
    @Contract("-> new")
    BlockData getBlockData();

    /**
     * Changes the block data for the visual.
     * @param blockData new block data for the world block
     */
    void setBlockData(BlockData blockData);

    // TODO Lighting

}
