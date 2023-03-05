package org.machinemc.server.world.blocks;

import lombok.*;
import org.machinemc.server.chunk.ChunkUtils;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.blocks.BlockVisual;
import org.machinemc.api.world.blocks.WorldBlock;

/**
 * Visual of a block that can be changed.
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DynamicVisual implements BlockVisual {

    @ToString.Exclude
    private final WorldBlock source;
    private BlockData blockData;

    @Override
    public BlockData getBlockData() {
        return blockData.clone();
    }

    @Override
    public void setBlockData(BlockData blockData) {
        if(this.blockData.getId() == blockData.getId()) return;
        this.blockData = blockData;
        final BlockPosition position = source.getPosition();
        final int offset = source.getWorld().getDimensionType().getMinY();
        source.getWorld().getChunk(position)
                .getSectionAt(position.getY() - offset)
                .getBlockPalette().set(
                        ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                        ChunkUtils.getSectionRelativeCoordinate(position.getY() - offset),
                        ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                        blockData.getId());
    }

}
