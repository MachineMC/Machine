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
package org.machinemc.api.particles;

import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.machinemc.nbt.NBTCompound;

/**
 * Represents additional options of a particle.
 */
public interface ParticleOption extends NBTSerializable, Writable {

    /**
     * Loads data from a compound to the options.
     * @param compound compound to load from
     */
    void load(NBTCompound compound);

    /**
     * Particle options implementation for particles that have no
     * additional data.
     */
    final class SimpleOptions implements ParticleOption {

        @Override
        public NBTCompound toNBT() {
            return new NBTCompound();
        }

        @Override
        public void write(final ServerBuffer buf) {

        }

        @Override
        public void load(final NBTCompound compound) {

        }

    }

}
