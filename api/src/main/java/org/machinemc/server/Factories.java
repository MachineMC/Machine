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
package org.machinemc.server;

import org.machinemc.api.inventory.Item;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.Material;
import org.machinemc.api.world.particles.Particle;
import org.machinemc.api.world.particles.ParticleType;
import org.jetbrains.annotations.ApiStatus;

/**
 * Internal class used for singleton factories
 * for default implementations of parts of the api.
 */
@ApiStatus.Internal
final class Factories {

    static ServerBufferCreator bufferFactory;
    static ItemCreator itemFactory;
    static ParticleCreator particleFactory;

    private Factories() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creator providing default buffer implementation.
     */
    public interface ServerBufferCreator {

        /**
         * @return new default server buffer
         */
        ServerBuffer create();

    }

    /**
     * Creator providing default item implementation.
     */
    public interface ItemCreator {

        /**
         * Creates new default item.
         * @param material material of the item
         * @param amount amount of the item
         * @return new item
         */
        Item create(Material material, byte amount);

    }

    /**
     * Creator providing default particle implementation.
     */
    public interface ParticleCreator {

        /**
         * Creates new default particle.
         * @param type type of the particle
         * @return new particle
         */
        Particle create(ParticleType type);

    }

}
