package me.pesekjak.machine.world.blocks;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@RequiredArgsConstructor
public class BlockType {

    @Getter
    protected final NamespacedKey name;
    @Getter
    protected final BlockProperties properties;

    public void create(WorldBlock block, CreateReason reason, @Nullable Entity source) {

    }

    public void destroy(WorldBlock block, DestroyReason reason, @Nullable Entity source) {

    }

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
