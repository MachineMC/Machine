package me.pesekjak.machine.world.blocks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.world.BlockData;
import me.pesekjak.machine.world.BlockDataImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Visual of a block that can't be changed.
 */
@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
@EqualsAndHashCode
public class FixedVisual implements BlockVisual {

    private final BlockData blockData;

    @Override
    public @NotNull BlockData getBlockData() {
        return blockData.clone();
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        throw new UnsupportedOperationException();
    }

}
