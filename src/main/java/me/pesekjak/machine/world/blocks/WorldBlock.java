package me.pesekjak.machine.world.blocks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.World;

@EqualsAndHashCode
@ToString
@Getter
public class WorldBlock {

    private final BlockType blockType;
    private final BlockPosition position;
    private final World world;
    private final BlockVisual visual;

    public WorldBlock(BlockType blockType, BlockPosition position, World world) {
        this.blockType = blockType;
        this.position = position;
        this.world = world;
        this.visual = blockType.visualizer.create(this);
    }

}
