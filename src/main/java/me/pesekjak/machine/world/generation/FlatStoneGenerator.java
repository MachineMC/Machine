package me.pesekjak.machine.world.generation;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.blocks.BlockManagerImpl;
import me.pesekjak.machine.world.blocks.BlockType;
import org.jetbrains.annotations.NotNull;

public class FlatStoneGenerator implements Generator {

    @Getter
    private final Machine server;
    @Getter
    private final long seed;

    private final BlockType air;
    private final BlockType stone;

    public FlatStoneGenerator(Machine server, long seed) {
        this.server = server;
        this.seed = seed;
        BlockManagerImpl manager = server.getBlockManager();
        air = manager.getBlockType(NamespacedKey.minecraft("air"));
        stone = manager.getBlockType(NamespacedKey.minecraft("stone"));
    }

    @Override
    public @NotNull BlockType generate(@NotNull BlockPosition position) {
        if(position.getY() > 1) return air;
        return stone;
    }

}
