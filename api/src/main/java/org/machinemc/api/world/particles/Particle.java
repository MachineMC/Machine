/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
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
