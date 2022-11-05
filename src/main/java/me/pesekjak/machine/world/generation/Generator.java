package me.pesekjak.machine.world.generation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.blocks.BlockType;

@RequiredArgsConstructor
@Getter
public abstract class Generator implements ServerProperty {

    private final Machine server;
    private final long seed;

    public abstract BlockType generate(BlockPosition position);

}
