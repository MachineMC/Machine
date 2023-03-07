package org.machinemc.api.world.particles;

import org.machinemc.server.Server;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.Writable;

import java.util.Optional;

/**
 * Represents a playable particle.
 */
public interface Particle extends NBTSerializable, Writable {

    /**
     * Creates new instance of the classic particle implementation.
     * @param type type of the particle
     * @return new particle
     * @throws UnsupportedOperationException if the creator hasn't been initialized
     */
    static Particle of(ParticleType type) {
        return Server.createParticle(type);
    }

    /**
     * @return type used by the particle
     */
    ParticleType getType();

    /**
     * @return options used by the particle
     */
    Optional<ParticleOptions> getOptions();

}
