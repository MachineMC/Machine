/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.api.world.blocks;

import org.jetbrains.annotations.Nullable;
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
     * Merges the provided compound to the nbt compound of the world block.
     * @param compound compound to merge
     */
    void mergeNBT(NBTCompound compound);

    /**
     * Updates the NBT of the world block.
     * @param compound new nbt
     */
    void setNBT(@Nullable NBTCompound compound);

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
     * @param world world of the block
     * @param position position of the block
     * @param blockType type of the block
     * @param compound nbt of the block
     */
    record State(World world,
                 BlockPosition position,
                 BlockType blockType,
                 NBTCompound compound) {

    }

}
