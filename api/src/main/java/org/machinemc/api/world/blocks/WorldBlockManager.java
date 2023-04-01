package org.machinemc.api.world.blocks;

import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;

/**
 * Managers multiple instances of world blocks in a single world,
 * makes sure that two instances of a world block at the same location
 * can't exist and is thread safe.
 */
public interface WorldBlockManager {

    /**
     * @return world source of the blocks
     */
    World getWorld();

    /**
     * Returns world block instance at given position in the world of this manager.
     * <p>
     * Creates new instance if there is not an existing one.
     * @param position position of the block
     * @return world block at given position
     */
    WorldBlock get(BlockPosition position);

}
