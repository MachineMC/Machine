package org.machinemc.api.world.blocks;

import org.machinemc.api.world.BlockData;
import org.jetbrains.annotations.Contract;

/**
 * Represents a visual of a block.
 */
public interface BlockVisual {

    @Contract("-> new")
    BlockData getBlockData();

    // TODO Lighting

}
