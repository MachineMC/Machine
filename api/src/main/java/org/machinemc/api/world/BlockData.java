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
package org.machinemc.api.world;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * Visual data of a block, to create a new instance
 * use {@link Material#createBlockData()}, each BlockData will
 * return unique id depending on its material and properties.
 */
// This class is used by the code generators, edit with caution.
public abstract class BlockData implements Cloneable {

    /**
     * Returns new instance of block data from id (mapped by vanilla server reports).
     * @param id id of the block data
     * @return new instance of the block data with the given id
     */
    public static BlockData getBlockData(final int id) {
        return BlockDataImpl.getBlockData(id);
    }

    /**
     * Finishes the registration of the block data to materials.
     */
    @ApiStatus.Internal
    public static void finishRegistration() {
        BlockDataImpl.finishRegistration();
    }

    /**
     * Registers the block data to the block data registry.
     */
    @ApiStatus.Internal
    protected abstract void register();

    /**
     * @return material of the block data
     */
    public abstract @Nullable Material getMaterial();

    /**
     * Changes base material for the block data and all its
     * variants.
     * @param material new material
     * @return this
     */
    @ApiStatus.Internal
    @Contract("_ -> this")
    protected abstract BlockData setMaterial(Material material);

    /**
     * @return id of the block data used by Minecraft protocol
     */
    public abstract int getId();

    /**
     * Returns map of keys and values of all properties of this block data.
     * Useful when creating data for block particles etc.
     * @return map of block data properties
     */
    public abstract @Unmodifiable Map<String, String> getDataMap();

    /**
     * @return clone of this block data
     */
    @Override
    public BlockData clone() {
        try {
            return (BlockData) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
