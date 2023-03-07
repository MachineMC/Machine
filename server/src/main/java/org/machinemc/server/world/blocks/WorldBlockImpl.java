package org.machinemc.server.world.blocks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.BlockVisual;
import org.machinemc.api.world.blocks.WorldBlock;

/**
 * Default world block implementation.
 */
// TODO Equals should check just location and world
//  and the block instance should be synced and only
//  one per world block
@EqualsAndHashCode
@ToString
@Getter
public class WorldBlockImpl implements WorldBlock {

    private final BlockType blockType;
    private final BlockPosition position;
    private final World world;
    private final BlockVisual visual;

    public WorldBlockImpl(BlockType blockType, BlockPosition position, World world) {
        this.blockType = blockType;
        this.position = position;
        this.world = world;
        this.visual = blockType.getVisualizer().create(this);
    }

}
