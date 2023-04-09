package org.machinemc.api.world.blocks;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
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
     * Removes multiple block types from the manager.
     * @param blockTypes block type to remove
     */
    default void removeBlocks(BlockType... blockTypes) {
        Arrays.stream(blockTypes).forEach(this::removeBlock);
    }

    default void removeBlock(NamespacedKey name) {
        BlockType block = getBlockType(name);
        if(block == null) return;
        removeBlock(block);
    }

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
    @Nullable BlockType getBlockType(NamespacedKey name);

    /**
     * @return unmodifiable set of all block types registered in this manager
     */
    @Unmodifiable Set<BlockType> getBlocks();

}
