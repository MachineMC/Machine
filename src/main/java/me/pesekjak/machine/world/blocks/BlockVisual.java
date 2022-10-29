package me.pesekjak.machine.world.blocks;

import lombok.*;
import me.pesekjak.machine.world.BlockData;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
@Getter @Setter
public class BlockVisual {

    private BlockData blockData;
    private int lightLevel;

}
