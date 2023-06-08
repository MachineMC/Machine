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
package org.machinemc.server.world.blocks;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.Server;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.Material;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.scriptive.style.HexColor;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default block manager implementation.
 */
public class ServerBlockManager implements BlockManager {

    private final Map<NamespacedKey, BlockType> blocks = new ConcurrentHashMap<>();
    @Getter
    private final Server server;

    public ServerBlockManager(final Server server) {
        this.server = server;
    }

    /**
     * Creates manager with default settings.
     * @param server server to create manager for
     * @return newly created manager
     */
    public static BlockManager createDefault(final Server server) {
        final ServerBlockManager manager = new ServerBlockManager(server);
        manager.addBlocks(
                new BlockTypeImpl(NamespacedKey.minecraft("air"), BlockTypeImpl.BlockProperties.builder()
                        .color(new HexColor(255, 255, 255)).isAir(true).transparent(true).build(),
                        Material.AIR.createBlockData()),
                new BlockTypeImpl(NamespacedKey.minecraft("stone"), BlockTypeImpl.BlockProperties.builder()
                        .color(new HexColor(Color.GRAY)).resistance(6).blockHardness(1.5F).build(),
                        Material.STONE.createBlockData()),
                new SignBlock()
        );
        return manager;
    }

    @Override
    public void addBlock(final BlockType blockType) {
        if (isRegistered(blockType.getName()))
            throw new IllegalStateException("Block '" + blockType.getName() + "' is already registered");
        blocks.put(blockType.getName(), blockType);
    }

    @Override
    public void removeBlock(final BlockType blockType) {
        blocks.remove(blockType.getName());
    }

    @Override
    public boolean isRegistered(final NamespacedKey name) {
        return blocks.containsKey(name);
    }

    @Override
    public boolean isRegistered(final BlockType blockType) {
        return blocks.containsValue(blockType);
    }

    @Override
    public @Nullable BlockType getBlockType(final NamespacedKey name) {
        return blocks.get(name);
    }

    @Override
    public Set<BlockType> getBlocks() {
        return Set.copyOf(blocks.values());
    }

    @Override
    public String toString() {
        return "ServerBlockManager("
                + "server=" + server
                + ')';
    }

}
