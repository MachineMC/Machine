package org.machinemc.api.world.blocks;

import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an existing block in a world.
 */
public interface WorldBlock {

    /**
     * @return type of the block
     */
    @NotNull BlockType getBlockType();

    /**
     * @return position of the block
     */
    @NotNull BlockPosition getPosition();

    /**
     * @return the world the block is in
     */
    @NotNull World getWorld();

    /**
     * @return visual of the block
     */
    @NotNull BlockVisual getVisual();

}
