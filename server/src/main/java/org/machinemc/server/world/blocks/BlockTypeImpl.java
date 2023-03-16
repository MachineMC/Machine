package org.machinemc.server.world.blocks;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.BlockVisual;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.nbt.NBTCompound;

import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Default implementation of block type.
 */
@RequiredArgsConstructor
public class BlockTypeImpl implements BlockType {

    @Getter
    private final NamespacedKey name;
    @Getter
    private final BlockProperties properties;
    private final Function<WorldBlock.Snapshot, BlockVisual> visualProvider;

    private final boolean dynamicVisual;
    private final boolean tileEntity;

    private BiFunction<World, BlockPosition, NBTCompound> initFunction;

    private Consumer<WorldBlock.Snapshot> placeConsumer, destroyConsumer, updateConsumer;

    public BlockTypeImpl(NamespacedKey name, BlockProperties properties, Function<WorldBlock.Snapshot, BlockVisual> visualProvider, boolean dynamicVisual, boolean tileEntity,
                         @Nullable BiFunction<World, BlockPosition, NBTCompound> initFunction,
                         @Nullable Consumer<WorldBlock.Snapshot> placeConsumer,
                         @Nullable Consumer<WorldBlock.Snapshot> destroyConsumer,
                         @Nullable Consumer<WorldBlock.Snapshot> updateConsumer) {
        this(name, properties, visualProvider, dynamicVisual, tileEntity);
        this.initFunction = initFunction;
        this.placeConsumer = placeConsumer;
        this.destroyConsumer = destroyConsumer;
        this.updateConsumer = updateConsumer;
    }

    @Override
    public BlockVisual getVisual(@Nullable WorldBlock.Snapshot block) {
        return visualProvider.apply(block);
    }

    @Override
    public boolean hasDynamicVisual() {
        return dynamicVisual;
    }

    @Override
    public boolean isTileEntity() {
        return false;
    }

    @Override
    public @NotNull WorldBlock.Snapshot init(World world, BlockPosition position) {
        return new WorldBlock.Snapshot(
                world,
                position,
                this,
                initFunction != null ? initFunction.apply(world, position) : null
        );
    }

    @Override
    public void place(WorldBlock.Snapshot block) {
        if(placeConsumer != null) placeConsumer.accept(block);
    }

    @Override
    public void destroy(WorldBlock.Snapshot block) {
        if(destroyConsumer != null) destroyConsumer.accept(block);
    }

    @Override
    public void update(WorldBlock.Snapshot block) {
        if(updateConsumer != null) updateConsumer.accept(block);
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
