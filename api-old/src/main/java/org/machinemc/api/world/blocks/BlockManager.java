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

import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Manager of multiple block types.
 */
public interface BlockManager extends ServerProperty {

    /**
     * Registers new block type to the manager.
     * @param blockType block type to register
     */
    void addBlock(BlockType blockType);

    /**
     * Registers multiple block types to the manager.
     * @param blockTypes block types to register
     */
    default void addBlocks(BlockType... blockTypes) {
        Arrays.stream(blockTypes).forEach(this::addBlock);
    }

    /**
     * Removes a block type from the manager.
     * @param blockType block type to remove
     */
    void removeBlock(BlockType blockType);

    /**
     * Removes block with provided name from the manager.
     * @param name name of the block to remove
     */
    default void removeBlock(NamespacedKey name) {
        getBlockType(name).ifPresent(this::removeBlock);
    }

    /**
     * Removes multiple block types from the manager.
     * @param blockTypes block type to remove
     */
    default void removeBlocks(BlockType... blockTypes) {
        Arrays.stream(blockTypes).forEach(this::removeBlock);
    }

    /**
     * Removes blocks with provided names from the manager.
     * @param names name of the blocks to remove
     */
    default void removeBlocks(NamespacedKey... names) {
        Arrays.stream(names).forEach(this::removeBlocks);
    }

    /**
     * Checks if block type with given name is registered in this manager.
     * @param name name of the block type
     * @return if the block with given name is registered
     */
    boolean isRegistered(NamespacedKey name);

    /**
     * Checks if the block type is registered in this manager.
     * @param blockType block type to check
     * @return if the given block type is registered in this manager
     */
    boolean isRegistered(BlockType blockType);

    /**
     * Returns block type with given name registered in this manager.
     * @param name name of the block type
     * @return block type with given name in this manager
     */
    Optional<BlockType> getBlockType(NamespacedKey name);

    /**
     * @return unmodifiable set of all block types registered in this manager
     */
    @Unmodifiable Set<BlockType> getBlocks();

}
