package org.machinemc.api.world.particles.options;

import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.particles.ParticleOptions;
import org.jetbrains.annotations.NotNull;
import org.machinemc.api.world.particles.ParticleType;

/**
 * Options used by {@link ParticleType#BLOCK}.
 */
public interface BlockOptions extends ParticleOptions {

    /**
     * @return block data used by the block particle
     */
    @NotNull BlockData getBlockData();

    /**
     * Changes the block data used by the block particle.
     * @param blockData new block data
     */
    void setBlockData(@NotNull BlockData blockData);

}
