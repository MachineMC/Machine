package org.machinemc.api.world.blocks;

import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;

/**
 * Represents an existing block in a world.
 */
public interface WorldBlock {

    /**
     * @return type of the block
     */
    BlockType getBlockType();

    /**
     * @return position of the block
     */
    BlockPosition getPosition();

    /**
     * @return the world the block is in
     */
    World getWorld();

    /**
     * @return visual of the block
     */
    BlockVisual getVisual();

}
