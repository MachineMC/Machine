package me.pesekjak.machine.world.blocks;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.pesekjak.machine.world.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * Visual of a block that can't be changed.
 */
@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class FixedVisual implements BlockVisual {

    private final @NotNull BlockData blockData;

    @Override
    public @NotNull BlockData getBlockData() {
        return blockData.clone();
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        throw new UnsupportedOperationException();
    }

}
