package me.pesekjak.machine.world.particles.options;

import me.pesekjak.machine.world.BlockData;
import me.pesekjak.machine.world.particles.ParticleOptions;
import org.jetbrains.annotations.NotNull;

/**
 * Options used by {@link me.pesekjak.machine.world.particles.ParticleType#BLOCK}.
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
