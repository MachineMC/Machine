package me.pesekjak.machine.world.blocks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.World;
import org.jetbrains.annotations.NotNull;

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
