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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.world.blocks;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.BlockData;

import java.awt.*;
import java.util.List;

/**
 * Represents a block type with name, properties and custom behaviour.
 */
public interface BlockType {

    /**
     * @return name of the block type
     */
    NamespacedKey getName();

    /**
     * @return block properties of the block type
     */
    BlockProperties getProperties();

    /**
     * Provides a blockdata visual for a block in a world.
     * @param block block
     * @return blockdata visual for the block
     * @apiNote block argument can be null in case
     * the visual isn't dynamic {@link BlockType#hasDynamicVisual()}
     */
    @Contract("_ -> new")
    BlockData getBlockData(@Nullable WorldBlock.State block);

    /**
     * Whether the visual of the block of this type is dynamic and
     * can change depending on block's data.
     * @return whether the visual is dynamic
     */
    boolean hasDynamicVisual();

    // TODO ticking

    /**
     * @return list of all block handlers of this block type
     */
    @Unmodifiable List<BlockHandler> getHandlers();

    /**
     * Adds a new block handler to this block type.
     * @param handler handler to add
     */
    void addHandler(BlockHandler handler);

    /**
     * Removes an existing handler from this block type.
     * @param handler handler to remove
     * @return whether the handler has been removed successfully
     */
    boolean removeHandler(BlockHandler handler);

    /**
     * Represents properties of block types.
     */
    interface BlockProperties {

        /**
         * @return color of the block type
         */
        Color getColor();

        /**
         * @return if the block type has a collision
         */
        boolean isHasCollision();

        /**
         * @return if the block is air
         */
        boolean isAir();

        /**
         * @return block hardness of the block type
         */
        float getBlockHardness();

        /**
         * @return if entities can spawn on the block type
         */
        boolean isAllowsSpawning();

        /**
         * @return if the block type is solid
         */
        boolean isSolidBlock();

        /**
         * @return if the block type is transparent
         */
        boolean isTransparent();

        /**
         * @return if the block type has dynamic shape
         */
        boolean isDynamicShape();

        // TODO sound

    }

}
