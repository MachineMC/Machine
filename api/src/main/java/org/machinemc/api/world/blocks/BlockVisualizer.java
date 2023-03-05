package org.machinemc.api.world.blocks;

import org.jetbrains.annotations.NotNull;

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
    @NotNull BlockVisual create(@NotNull WorldBlock source);

}
