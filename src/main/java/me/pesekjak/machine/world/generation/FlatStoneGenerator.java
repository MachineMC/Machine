package me.pesekjak.machine.world.generation;

import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.blocks.BlockManager;
import me.pesekjak.machine.world.blocks.BlockType;

public class FlatStoneGenerator extends Generator {

    private final BlockType air;
    private final BlockType stone;

    public FlatStoneGenerator(Machine server, long seed) {
        super(server, seed);
        BlockManager manager = server.getBlockManager();
        air = manager.getBlockType(NamespacedKey.minecraft("air"));
        stone = manager.getBlockType(NamespacedKey.minecraft("stone"));
    }

    @Override
    public BlockType generate(BlockPosition position) {
        if(position.getY() > 1) return air;
        return stone;
    }

}
