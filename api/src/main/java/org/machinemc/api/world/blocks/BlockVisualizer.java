package org.machinemc.api.world.blocks;

/**
 * Creates initial visual for a created world block.
 */
@FunctionalInterface
public interface BlockVisualizer {

    /**
     * Creates initial the visual for a created world block.
     * @param source world block
     * @return created visual
     */
    BlockVisual create(WorldBlock source);

}
