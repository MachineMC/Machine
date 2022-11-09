package me.pesekjak.machine.world.blocks;

import lombok.*;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Represents a type of a block with given name, properties (hardness, color, resistance etc.),
 * and custom behaviour.
 */
@AllArgsConstructor
public class BlockType {

    @Getter
    protected final NamespacedKey name;
    @Getter
    protected final BlockProperties properties;
    protected BlockVisualizer visualizer;

    /**
     * Called when a new world block of this block type is created.
     * @param block created world block
     * @param reason reason why the world block was created
     * @param source source of the creation
     */
    public void create(WorldBlock block, CreateReason reason, @Nullable Entity source) {

    }

    /**
     * Called when a world block of this block type is removed.
     * @param block removed world block
     * @param reason reason why the world block was removed
     * @param source source of the removal
     */
    public void destroy(WorldBlock block, DestroyReason reason, @Nullable Entity source) {

    }

    /**
     * Called when a world block of this type is updated.
     * @param block updated world block
     */
    public void update(WorldBlock block) {

    }

    @Data
    @Builder
    public static class BlockProperties {

        @Builder.Default private Color color = Color.BLACK;
        @Builder.Default private boolean hasCollision = true;
        private float resistance;
        @Builder.Default private boolean isAir = false;
        private float blockHardness;
        @Builder.Default private boolean allowsSpawning = true;
        @Builder.Default private boolean solidBlock = true;
        private boolean transparent;
        private boolean dynamicShape;
        // TODO sound

    }

    public enum CreateReason {
        GENERATED,
        SET,
        PLACED,
        OTHER
    }

    public enum DestroyReason {
        REMOVED,
        EXPLOSION,
        DESTROYED,
        OTHER
    }

}
