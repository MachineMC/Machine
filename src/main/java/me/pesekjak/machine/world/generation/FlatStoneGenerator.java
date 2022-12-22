package me.pesekjak.machine.world.generation;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.blocks.BlockManager;
import me.pesekjak.machine.world.blocks.BlockType;
import org.jetbrains.annotations.NotNull;

/**
 * Simple flat world stone generator.
 */
@Getter
public class FlatStoneGenerator implements Generator {

    private final @NotNull Machine server;
    private final long seed;

    private final @NotNull BlockType air;
    private final @NotNull BlockType stone;

    public FlatStoneGenerator(@NotNull Machine server, long seed) {
        this.server = server;
        this.seed = seed;
        final BlockManager manager = server.getBlockManager();
        final BlockType air = manager.getBlockType(NamespacedKey.minecraft("air"));
        final BlockType stone = manager.getBlockType(NamespacedKey.minecraft("stone"));
        if(air == null || stone == null) throw new IllegalStateException();
        this.air = air;
        this.stone = stone;
    }

    @Override
    public @NotNull BlockType generate(@NotNull BlockPosition position) {
        if(position.getY() > 1) return air;
        return stone;
    }

}
