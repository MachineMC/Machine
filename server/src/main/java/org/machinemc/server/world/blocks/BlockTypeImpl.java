package org.machinemc.server.world.blocks;

import lombok.*;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.BlockVisualizer;
import org.machinemc.api.world.blocks.WorldBlock;

import java.awt.*;

/**
 * Default implementation of block type.
 */
@AllArgsConstructor
@Getter
public class BlockTypeImpl implements BlockType {

    private final NamespacedKey name;
    private final BlockProperties properties;
    protected BlockVisualizer visualizer;

    @Override
    public void create(WorldBlock block, CreateReason reason, @Nullable Entity source) {

    }

    @Override
    public void destroy(WorldBlock block, DestroyReason reason, @Nullable Entity source) {

    }

    @Override
    public void update(WorldBlock block) {

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
