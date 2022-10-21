package me.pesekjak.machine.world.blocks;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.Material;
import me.pesekjak.machine.world.World;

@Data
public class WorldBlock {

    @Getter
    private final BlockType blockType;
    @Getter
    private final BlockPosition position;
    @Getter
    private final World world;
    @Getter @Setter
    private BlockVisual visual = new BlockVisual(Material.AIR.createBlockData(), 0);

}
