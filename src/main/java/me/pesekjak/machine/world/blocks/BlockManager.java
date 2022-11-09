package me.pesekjak.machine.world.blocks;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.Material;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages multiple block types of the server.
 */
public class BlockManager implements ServerProperty {

    private final Map<NamespacedKey, BlockType> blocks = new ConcurrentHashMap<>();
    @Getter
    private final Machine server;

    public BlockManager(Machine server) {
        this.server = server;
    }

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static BlockManager createDefault(Machine server) {
        BlockManager manager = new BlockManager(server);
        manager.addBlocks(
                new BlockType(NamespacedKey.minecraft("air"), BlockType.BlockProperties.builder()
                        .color(new Color(255, 255, 255, 0)).isAir(true).transparent(true).build(),
                        ((source) -> new FixedVisual(Material.AIR.createBlockData()))),
                new BlockType(NamespacedKey.minecraft("stone"), BlockType.BlockProperties.builder()
                        .color(Color.GRAY).resistance(6).blockHardness(1.5F).build(),
                        ((source) -> new DynamicVisual(source, Material.STONE.createBlockData())))
        );
        return manager;
    }

    /**
     * Registers the block type to this manager if another block type with the same
     * name isn't registered.
     * @param blockType block type to register
     */
    public void addBlock(BlockType blockType) {
        if(blocks.containsKey(blockType.name))
            throw new IllegalStateException("Block '" + blockType.name + "' is already registered");
        blocks.put(blockType.getName(), blockType);
    }

    /**
     * Registers multiple block types to this manager if another block type with the same
     * name isn't registered.
     * @param blockTypes block types to register
     */
    public void addBlocks(BlockType... blockTypes) {
        Arrays.stream(blockTypes).forEach(this::addBlock);
    }

    /**
     * Removes the block type from the manager.
     * @param blockType block type that should be removed
     */
    public void removeBlock(BlockType blockType) {
        blocks.remove(blockType.name);
    }

    /**
     * Removes multiple block types from the manager.
     * @param blockTypes block types that should be removed
     */
    public void removeBlocks(BlockType... blockTypes) {
        Arrays.stream(blockTypes).forEach(this::removeBlock);
    }

    /**
     * Checks if a block type with given name is registered in this manager.
     * @param name name of the block type
     * @return true if block type with given name is registered in this manager
     */
    public boolean isRegistered(NamespacedKey name) {
        return blocks.containsKey(name);
    }

    /**
     * Checks if the given block type is registered in this manager.
     * @param blockType block type
     * @return true if the block type is registered in this manager
     */
    public boolean isRegistered(BlockType blockType) {
        return blocks.containsValue(blockType);
    }

    /**
     * Searches for registered block type with the given name in this manager.
     * @param name name of the block type to search for
     * @return block type with the given name
     */
    public BlockType getBlockType(NamespacedKey name) {
        return blocks.get(name);
    }

    /**
     * Collection of all registered block types in this manager
     * @return collection of all registered block types
     */
    public Set<BlockType> getBlocks() {
        return Set.copyOf(blocks.values());
    }

}
