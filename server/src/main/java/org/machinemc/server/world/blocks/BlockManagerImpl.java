package org.machinemc.server.world.blocks;

import lombok.Getter;
import org.machinemc.api.world.Material;
import org.machinemc.server.Machine;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.api.world.blocks.BlockType;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default block manager implementation.
 */
public class BlockManagerImpl implements BlockManager {

    private final Map<NamespacedKey, BlockType> blocks = new ConcurrentHashMap<>();
    @Getter
    private final Machine server;

    public BlockManagerImpl(Machine server) {
        this.server = server;
    }

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static BlockManager createDefault(Machine server) {
        final BlockManagerImpl manager = new BlockManagerImpl(server);
        manager.addBlocks(
                new BlockTypeImpl(NamespacedKey.minecraft("air"), BlockTypeImpl.BlockProperties.builder()
                        .color(new Color(255, 255, 255, 0)).isAir(true).transparent(true).build(),
                        ((source) -> new FixedVisual(Material.AIR.createBlockData()))),
                new BlockTypeImpl(NamespacedKey.minecraft("stone"), BlockTypeImpl.BlockProperties.builder()
                        .color(Color.GRAY).resistance(6).blockHardness(1.5F).build(),
                        ((source) -> new DynamicVisual(source, Material.STONE.createBlockData())))
        );
        return manager;
    }

    @Override
    public void addBlock(BlockType blockType) {
        if(blocks.containsKey(blockType.getName()))
            throw new IllegalStateException("Block '" + blockType.getName() + "' is already registered");
        blocks.put(blockType.getName(), blockType);
    }

    @Override
    public void removeBlock(BlockType blockType) {
        blocks.remove(blockType.getName());
    }

    @Override
    public boolean isRegistered(NamespacedKey name) {
        return blocks.containsKey(name);
    }

    @Override
    public boolean isRegistered(BlockType blockType) {
        return blocks.containsValue(blockType);
    }

    @Override
    public @Nullable BlockType getBlockType(NamespacedKey name) {
        return blocks.get(name);
    }

    @Override
    public Set<BlockType> getBlocks() {
        return Set.copyOf(blocks.values());
    }

}
