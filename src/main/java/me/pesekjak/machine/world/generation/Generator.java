package me.pesekjak.machine.world.generation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.blocks.BlockType;

/**
 * World generator
 */
@RequiredArgsConstructor
@Getter
public abstract class Generator implements ServerProperty {

    private final Machine server;
    private final long seed;

    /**
     * Called for each block generated in a world with this generator.
     * @param position position of the block
     * @return block type to generate
     */
    public abstract BlockType generate(BlockPosition position);

}
