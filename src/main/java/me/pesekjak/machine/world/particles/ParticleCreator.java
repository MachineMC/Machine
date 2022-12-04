package me.pesekjak.machine.world.particles;

import me.pesekjak.machine.utils.ServerBuffer;

/**
 * Creates particles from the type and buffer.
 */
@FunctionalInterface
public interface ParticleCreator {

    ParticleCreator empty = (type, buf) -> ParticleImpl.of(type);

    /**
     * Creates the Particle from the type and buffer with its options data
     * @param type type of the particle
     * @param buf buffer with data of the particle options
     * @return created Particle
     */
    ParticleImpl create(ParticleType type, ServerBuffer buf);

}
