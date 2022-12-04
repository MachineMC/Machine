package me.pesekjak.machine.world.blocks;

import me.pesekjak.machine.world.BlockData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents visual of a world block.
 */
public interface BlockVisual {

    /**
     * @return clone of the block data currently used by the visual
     */
    @Contract("-> new")
    @NotNull BlockData getBlockData();

    /**
     * Changes the block data for the visual.
     * @param blockData new block data for the world block
     */
    void setBlockData(@NotNull BlockData blockData);

    // TODO Lighting

}
