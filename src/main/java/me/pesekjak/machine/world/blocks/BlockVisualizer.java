package me.pesekjak.machine.world.blocks;

@FunctionalInterface
public interface BlockVisualizer {

    BlockVisual create(WorldBlock source);

}
