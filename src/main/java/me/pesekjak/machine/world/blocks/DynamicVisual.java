package me.pesekjak.machine.world.blocks;

import lombok.*;
import me.pesekjak.machine.chunk.ChunkUtils;
import me.pesekjak.machine.world.BlockData;
import me.pesekjak.machine.world.BlockPosition;
import org.jetbrains.annotations.NotNull;

/**
 * Visual of a block that can be changed.
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DynamicVisual implements BlockVisual {

    @ToString.Exclude
    private final @NotNull WorldBlock source;
    private @NotNull BlockData blockData;

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
        source.getWorld().getChunk(position)
                .getSectionAt(position.getY() - offset)
                .getBlockPalette().set(
                        ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                        ChunkUtils.getSectionRelativeCoordinate(position.getY() - offset),
                        ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                        blockData.getId());
    }

}
