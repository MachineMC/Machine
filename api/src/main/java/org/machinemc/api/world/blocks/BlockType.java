package org.machinemc.api.world.blocks;

import org.machinemc.api.entities.Entity;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Represents a block type with name, properties and custom behaviour.
 */
public interface BlockType {

    /**
     * @return name of the block type
     */
    NamespacedKey getName();

    /**
     * @return block properties of the block type
     */
    BlockProperties getProperties();

    /**
     * @return block visualizer of the block type
     */
    BlockVisualizer getVisualizer();

    /**
     * Called when a new world block of this block type is created.
     * @param block created world block
     * @param reason reason why the world block was created
     * @param source source of the creation
     */
    void create(WorldBlock block, CreateReason reason, @Nullable Entity source);

    /**
     * Called when a world block of this block type is removed.
     * @param block removed world block
     * @param reason reason why the world block was removed
     * @param source source of the removal
     */
    void destroy(WorldBlock block, DestroyReason reason, @Nullable Entity source);

    /**
     * Called when a world block of this type is updated.
     * @param block updated world block
     */
    void update(WorldBlock block);

    /**
     * Reasons of block creation.
     */
    enum CreateReason {
        GENERATED,
        SET,
        PLACED,
        OTHER
    }

    /**
     * Reasons of block removal.
     */
    enum DestroyReason {
        REMOVED,
        EXPLOSION,
        DESTROYED,
        OTHER
    }

    /**
     * Represents properties of block types.
     */
    interface BlockProperties {

        /**
         * @return color of the block type
         */
        Color getColor();

        /**
         * @return if the block type has a collision
         */
        boolean isHasCollision();

        /**
         * @return if the block is air
         */
        boolean isAir();

        /**
         * @return block hardness of the block type
         */
        float getBlockHardness();

        /**
         * @return if entities can spawn on the block type
         */
        boolean isAllowsSpawning();

        /**
         * @return if the block type is solid
         */
        boolean isSolidBlock();

        /**
         * @return if the block type is transparent
         */
        boolean isTransparent();

        /**
         * @return if the block type has dynamic shape
         */
        boolean isDynamicShape();

        // TODO sound

    }

}
