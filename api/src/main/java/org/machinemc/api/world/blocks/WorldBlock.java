package org.machinemc.api.world.blocks;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.nbt.NBTCompound;

/**
 * Represents an existing block in a world.
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
     * @return nbt of the block
     */
    NBTCompound getNBT();

    /**
     * @return visual of the block
     */
    @Contract("-> new")
    default BlockVisual getVisual() {
        return getBlockType().getVisual(asSnapshot());
    }

    /**
     * Updates a visual of this block, isn't persistent, the visual is updated
     * only for players that has the chunk with this world block loaded.
     * @param visual new visual
     */
    void setVisual(BlockVisual visual);

    default Snapshot asSnapshot() {
        return new Snapshot(getWorld(), getPosition(), getBlockType(), getNBT());
    }

    // TODO ticking

    /**
     * Snapshot of the world block, is used for fast getting
     * WorldBlock snapshots from file or while world generation.
     */
    record Snapshot(World world,
                    BlockPosition position,
                    BlockType blockType,
                    @Nullable NBTCompound compound
                    ) {

    }

}
