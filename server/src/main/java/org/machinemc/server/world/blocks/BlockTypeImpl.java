package org.machinemc.server.world.blocks;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.blocks.BlockHandler;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Default implementation of block type.
 */
@RequiredArgsConstructor
public class BlockTypeImpl implements BlockType {

    @Getter
    private final NamespacedKey name;
    @Getter
    private final BlockType.BlockProperties properties;

    private final Function<WorldBlock.State, BlockData> blockDataProvider;
    private final boolean dynamicVisual;

    private final List<BlockHandler> handlers = new CopyOnWriteArrayList<>();

    public BlockTypeImpl(NamespacedKey name,
                         BlockType.BlockProperties properties,
                         BlockData defaultBlockData) {
        this.name = name;
        this.properties = properties;
        blockDataProvider = (state -> defaultBlockData);
        dynamicVisual = false;
    }

    public BlockTypeImpl(NamespacedKey name,
                         BlockType.BlockProperties properties,
                         Function<WorldBlock.State, BlockData> blockDataProvider,
                         boolean dynamicVisual,
                         BlockHandler defaultHandler) {
        this(name, properties, blockDataProvider, dynamicVisual);
        addHandler(defaultHandler);
    }

    @Override
    public BlockData getBlockData(WorldBlock.@Nullable State block) {
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
    public void addHandler(BlockHandler handler) {
        handlers.add(handler);
    }

    @Override
    public boolean removeHandler(BlockHandler handler) {
        return handlers.remove(handler);
    }

    /**
     * Default block properties implementation.
     */
    @Data
    @Builder
    public static class BlockProperties implements BlockType.BlockProperties {

        @Builder.Default private Color color = Color.BLACK;
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
