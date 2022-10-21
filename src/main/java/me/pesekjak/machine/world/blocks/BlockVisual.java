package me.pesekjak.machine.world.blocks;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.world.BlockData;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockVisual {

    @Getter @Setter
    private BlockData blockData;
    @Getter @Setter
    private int lightLevel;

}
