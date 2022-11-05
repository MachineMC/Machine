package me.pesekjak.machine.world.blocks;

import lombok.*;
import me.pesekjak.machine.chunk.ChunkUtils;
import me.pesekjak.machine.world.BlockData;
import me.pesekjak.machine.world.BlockPosition;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class DynamicVisual implements BlockVisual {

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

    public String toString() {
        return "BlockVisual(" + blockData.toString() + ")";
    }

}
