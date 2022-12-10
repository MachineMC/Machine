package me.pesekjak.machine.world.blocks;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default block manager implementation.
 */
public class BlockManagerImpl implements BlockManager {

    private final @NotNull Map<NamespacedKey, BlockType> blocks = new ConcurrentHashMap<>();
    @Getter
    private final @NotNull Machine server;

    public BlockManagerImpl(@NotNull Machine server) {
        this.server = server;
    }

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static @NotNull BlockManager createDefault(@NotNull Machine server) {
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
    public void addBlock(@NotNull BlockType blockType) {
        if(blocks.containsKey(blockType.getName()))
            throw new IllegalStateException("Block '" + blockType.getName() + "' is already registered");
        blocks.put(blockType.getName(), blockType);
    }

    @Override
    public void removeBlock(@NotNull BlockType blockType) {
        blocks.remove(blockType.getName());
    }

    @Override
    public boolean isRegistered(@NotNull NamespacedKey name) {
        return blocks.containsKey(name);
    }

    @Override
    public boolean isRegistered(@NotNull BlockType blockType) {
        return blocks.containsValue(blockType);
    }

    @Override
    public @Nullable BlockType getBlockType(@NotNull NamespacedKey name) {
        return blocks.get(name);
    }

    @Override
    public @NotNull Set<BlockType> getBlocks() {
        return Set.copyOf(blocks.values());
    }

}
