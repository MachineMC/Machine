package org.machinemc.api.world.blocks;

import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.nbt.NBTCompound;

/**
 * Represents an existing block in a world.
 * <p>
 * This is a live object, and only one may exist for any given location in a world.
 * The state or nbt of the block may change concurrently to your own handling of it;
 * use {@link WorldBlock#asState} to get a snapshot of the block which will not be modified.
 */
public interface WorldBlock {

    /**
     * @return the world the block is in
     */
    World getWorld();

    /**
     * @return position of the block
     */
    BlockPosition getPosition();

    /**
     * @return type of the block
     */
    BlockType getBlockType();

    /**
     * Changes the block type of the world block.
     * @param blockType new block type
     */
    void setBlockType(BlockType blockType);

    /**
     * @return clone of the nbt of the block
     */
    NBTCompound getNBT();

    /**
     * Updates the NBT of the world block.
     * @param compound new nbt
     */
    void setNBT(NBTCompound compound);

    /**
     * @return blockdata of the block
     */
    BlockData getBlockData();

    /**
     * Returns the current snapshot of the block as block state.
     * @return state of this block
     */
    default State asState() {
        return new State(getWorld(), getPosition(), getBlockType(), getNBT());
    }

    /**
     * Represents a state of a block, doesn't contain the current information
     * about the block, acts just like a snapshot.
     */
    record State(World world,
                 BlockPosition position,
                 BlockType blockType,
                 NBTCompound compound) {

    }

}
