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
package org.machinemc.api.world.particles;

import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.Writable;

/**
 * Represents an option for the particle type.
 */
public interface ParticleOptions extends NBTSerializable, Writable, Cloneable {

    /**
     * @return clone of this particle options
     */
    ParticleOptions clone();

}
