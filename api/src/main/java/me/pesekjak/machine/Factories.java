package me.pesekjak.machine;

import lombok.experimental.UtilityClass;
import me.pesekjak.machine.inventory.Item;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.Material;
import me.pesekjak.machine.world.particles.Particle;
import me.pesekjak.machine.world.particles.ParticleType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Internal class used for singleton factories
 * for default implementations of parts of the api.
 */
@ApiStatus.Internal
@UtilityClass
class Factories {

    static ServerBufferCreator BUFFER_FACTORY;
    static ItemCreator ITEM_FACTORY;
    static ParticleCreator PARTICLE_FACTORY;

    /**
     * Creator providing default buffer implementation.
     */
    @ApiStatus.NonExtendable
    public interface ServerBufferCreator {

        /**
         * @return new default server buffer
         */
        @NotNull ServerBuffer create();

    }

    /**
     * Creator providing default item implementation.
     */
    @ApiStatus.NonExtendable
    public interface ItemCreator {

        /**
         * Creates new default item.
         * @param material material of the item
         * @param amount amount of the item
         * @return new item
         */
        @NotNull Item create(@NotNull Material material, byte amount);

    }

    /**
     * Creator providing default particle implementation.
     */
    @ApiStatus.NonExtendable
    public interface ParticleCreator {

        /**
         * Creates new default particle.
         * @param type type of the particle
         * @return new particle
         */
        @NotNull Particle create(@NotNull ParticleType type);

    }

}
