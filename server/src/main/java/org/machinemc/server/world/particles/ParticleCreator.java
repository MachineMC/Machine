package org.machinemc.server.world.particles;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.particles.Particle;
import org.machinemc.api.world.particles.ParticleType;

/**
 * Creates particles from the type and buffer.
 */
@FunctionalInterface
public interface ParticleCreator {

    ParticleCreator EMPTY = (type, buf) -> ParticleImpl.of(type);

    /**
     * Creates the Particle from the type and buffer with its options data.
     * @param type type of the particle
     * @param buf buffer with data of the particle options
     * @return created Particle
     */
    Particle create(ParticleType type, ServerBuffer buf);

}
