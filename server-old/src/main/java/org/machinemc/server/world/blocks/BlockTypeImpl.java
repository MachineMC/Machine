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

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.blocks.BlockHandler;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Default implementation of block type.
 */
public class BlockTypeImpl implements BlockType {

    @Getter
    private final NamespacedKey name;
    @Getter
    private final BlockType.BlockProperties properties;

    private final @Nullable Function<WorldBlock.State, BlockData> blockDataProvider;
    private final boolean dynamicVisual;

    private final List<BlockHandler> handlers = new CopyOnWriteArrayList<>();

    public BlockTypeImpl(final NamespacedKey name,
                         final BlockType.BlockProperties properties,
                         final BlockData defaultBlockData) {
        this.name = Objects.requireNonNull(name, "Block type name can not be null");
        this.properties = Objects.requireNonNull(properties, "Block type properties can not be null");
        blockDataProvider = state -> defaultBlockData;
        dynamicVisual = false;
    }

    public BlockTypeImpl(final NamespacedKey name,
                         final BlockType.BlockProperties properties,
                         final @Nullable Function<WorldBlock.State, BlockData> blockDataProvider,
                         final boolean dynamicVisual) {
        this.name = Objects.requireNonNull(name, "Block type name can not be null");
        this.properties = Objects.requireNonNull(properties, "Block type properties can not be null");
        this.blockDataProvider = blockDataProvider;
        this.dynamicVisual = dynamicVisual;
    }

    public BlockTypeImpl(final NamespacedKey name,
                         final BlockType.BlockProperties properties,
                         final @Nullable Function<WorldBlock.State, BlockData> blockDataProvider,
                         final boolean dynamicVisual,
                         final BlockHandler defaultHandler) {
        this(name, properties, blockDataProvider, dynamicVisual);
        addHandler(Objects.requireNonNull(defaultHandler, "Default handler can not be null"));
    }

    @Override
    public BlockData getBlockData(final WorldBlock.@Nullable State block) {
        if (blockDataProvider == null)
            throw new RuntimeException("Can not provide block data for " + getName() + " block type");
        return blockDataProvider.apply(block);
    }

    @Override
    public boolean hasDynamicVisual() {
        return dynamicVisual;
    }

    @Override
    public @Unmodifiable List<BlockHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    @Override
    public void addHandler(final BlockHandler handler) {
        handlers.add(handler);
    }

    @Override
    public boolean removeHandler(final BlockHandler handler) {
        return handlers.remove(handler);
    }

    @Override
    public String toString() {
        return "BlockType("
                + "name=" + name
                + ')';
    }

    /**
     * Default block properties implementation.
     */
    @Data
    @Builder
    public static class BlockProperties implements BlockType.BlockProperties {

        @Builder.Default private Colour color = ChatColor.BLACK;
        @Builder.Default private boolean hasCollision = true;
        private float resistance;
        private boolean isAir;
        private float blockHardness;
        @Builder.Default private boolean allowsSpawning = true;
        @Builder.Default private boolean solidBlock = true;
        private boolean transparent;
        private boolean dynamicShape;

    }

}
