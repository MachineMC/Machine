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
