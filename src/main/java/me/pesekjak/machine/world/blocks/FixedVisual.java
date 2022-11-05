package me.pesekjak.machine.world.blocks;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.world.BlockData;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
@EqualsAndHashCode
public class FixedVisual implements BlockVisual {

    private final BlockData blockData;

    @Override
    public BlockData getBlockData() {
        return blockData;
    }

    @Override
    public void setBlockData(BlockData blockData) {
        throw new UnsupportedOperationException();
    }

}
