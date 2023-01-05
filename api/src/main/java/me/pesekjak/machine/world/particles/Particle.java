package me.pesekjak.machine.world.particles;

import me.pesekjak.machine.Server;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.Writable;
import org.jetbrains.annotations.NotNull;

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
    static @NotNull Particle of(@NotNull ParticleType type) {
        return Server.createParticle(type);
    }

    /**
     * @return type used by the particle
     */
    @NotNull ParticleType getType();

    /**
     * @return options used by the particle
     */
    @NotNull Optional<ParticleOptions> getOptions();

}
