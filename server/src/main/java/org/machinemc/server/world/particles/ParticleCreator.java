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
 * If not, see <https://www.gnu.org/licenses/>.
 */
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
