package me.pesekjak.machine.world.particles;

import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.Writable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents a playable particle.
 */
public interface Particle extends NBTSerializable, Writable {

    /**
     * @return type used by the particle
     */
    @NotNull ParticleType getType();

    /**
     * @return options used by the particle
     */
    @NotNull Optional<ParticleOptions> getOptions();

}
