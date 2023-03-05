package org.machinemc.api.world.blocks;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Set;

/**
 * Manager of multiple block types.
 */
public interface BlockManager extends ServerProperty {

    /**
     * Registers new block type to the manager.
     * @param blockType block type to register
     */
    void addBlock(@NotNull BlockType blockType);

    /**
     * Registers multiple block types to the manager.
     * @param blockTypes block types to register
     */
    default void addBlocks(BlockType @NotNull ... blockTypes) {
        Arrays.stream(blockTypes).forEach(this::addBlock);
    }

    /**
     * Removes a block type from the manager.
     * @param blockType block type to remove
     */
    void removeBlock(@NotNull BlockType blockType);

    /**
     * Removes multiple block types from the manager.
     * @param blockTypes block type to remove
     */
    default void removeBlocks(BlockType @NotNull ... blockTypes) {
        Arrays.stream(blockTypes).forEach(this::removeBlock);
    }

    /**
     * Checks if block type with given name is registered in this manager.
     * @param name name of the block type
     * @return if the block with given name is registered
     */
    boolean isRegistered(@NotNull NamespacedKey name);

    /**
     * Checks if the block type is registered in this manager.
     * @param blockType block type to check
     * @return if the given block type is registered in this manager
     */
    boolean isRegistered(@NotNull BlockType blockType);

    /**
     * Returns block type with given name registered in this manager.
     * @param name name of the block type
     * @return block type with given name in this manager
     */
    @Nullable BlockType getBlockType(@NotNull NamespacedKey name);

    /**
     * @return unmodifiable set of all block types registered in this manager
     */
    @Unmodifiable @NotNull Set<BlockType> getBlocks();

}
