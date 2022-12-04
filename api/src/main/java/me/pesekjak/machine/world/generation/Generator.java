package me.pesekjak.machine.world.generation;

import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.blocks.BlockType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents generator of a world.
 */
public interface Generator extends ServerProperty {

    /**
     * Returns a block type that should generate at the given block position
     * @param position position to generate
     * @return block type to generate at that position
     */
    @NotNull BlockType generate(@NotNull BlockPosition position);

}
