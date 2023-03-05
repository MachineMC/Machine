package org.machinemc.server.world.blocks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.jetbrains.annotations.NotNull;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.BlockVisual;
import org.machinemc.api.world.blocks.WorldBlock;

/**
 * Default world block implementation.
 */
@EqualsAndHashCode
@ToString
@Getter
public class WorldBlockImpl implements WorldBlock {

    private final @NotNull BlockType blockType;
    private final @NotNull BlockPosition position;
    private final @NotNull World world;
    private final @NotNull BlockVisual visual;

    public WorldBlockImpl(@NotNull BlockType blockType, @NotNull BlockPosition position, @NotNull World world) {
        this.blockType = blockType;
        this.position = position;
        this.world = world;
        this.visual = blockType.getVisualizer().create(this);
    }

}
