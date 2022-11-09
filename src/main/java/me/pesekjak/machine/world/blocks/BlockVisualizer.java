package me.pesekjak.machine.world.blocks;

/**
 * Creates initial visual for a created WorldBlock.
 */
@FunctionalInterface
public interface BlockVisualizer {

    /**
     * Creates initial the visual for a created WorldBlock
     * @param source world block
     * @return created visual
     */
    BlockVisual create(WorldBlock source);

}
