package me.pesekjak.machine.world.blocks;

import me.pesekjak.machine.world.BlockData;

// TODO Lighting
/**
 * Represents visual of a WorldBlock
 */
public interface BlockVisual {

    /**
     * @return blockdata currently used by the world block
     */
    BlockData getBlockData();

    /**
     * @param blockData new blockdata for the world block
     */
    void setBlockData(BlockData blockData);

}
