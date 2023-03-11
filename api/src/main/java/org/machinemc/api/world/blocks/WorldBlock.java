package org.machinemc.api.world.blocks;

import org.jetbrains.annotations.Contract;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.nbt.NBTCompound;

/**
 * Represents an existing block in a world.
 */
public interface WorldBlock {

    /**
     * @return type of the block
     */
    BlockType getBlockType();

    void setBlockType(BlockType blockType);

    /**
     * @return position of the block
     */
    BlockPosition getPosition();

    /**
     * @return the world the block is in
     */
    World getWorld();

    /**
     * @return nbt of the block
     */
    NBTCompound getNBT();

    /**
     * @return visual of the block
     */
    @Contract("-> new")
    default BlockVisual getVisual() {
        return getBlockType().getVisual(this);
    }

    /**
     * Updates a visual of this block, isn't persistent, the visual is updated
     * only for players that has the chunk with this world block loaded.
     * @param visual new visual
     */
    void setVisual(BlockVisual visual);

    // TODO ticking

}
