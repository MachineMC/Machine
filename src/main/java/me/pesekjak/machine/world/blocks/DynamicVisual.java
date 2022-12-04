package me.pesekjak.machine.world.blocks;

import lombok.*;
import me.pesekjak.machine.chunk.ChunkUtils;
import me.pesekjak.machine.world.BlockData;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.WorldImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Changeable visual of a block.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class DynamicVisual implements BlockVisual {

    private final WorldBlock source;
    private BlockData blockData;

    @Override
    public @NotNull BlockData getBlockData() {
        return blockData.clone();
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        if(this.blockData.getId() == blockData.getId()) return;
        this.blockData = blockData;
        final BlockPosition position = source.getPosition();
        final int offset = source.getWorld().getDimensionType().getMinY();
        ((WorldImpl) source.getWorld()).getChunk(position) // TODO cleanup
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
