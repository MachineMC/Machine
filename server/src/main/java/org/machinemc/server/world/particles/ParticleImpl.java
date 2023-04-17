package org.machinemc.server.world.particles;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.particles.Particle;
import org.machinemc.api.world.particles.ParticleOptions;
import org.machinemc.api.world.particles.ParticleType;

import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of the particle.
 */
@ToString
public final class ParticleImpl implements Particle {

    @Getter @Setter
    private ParticleType type;
    @Setter
    private ParticleOptions options;

    private ParticleImpl(final ParticleType type) {
        this.type = type;
    }

    /**
     * Creates new particle from the given type.
     * @param type type of the particle
     * @return particle created from the given type
     */
    public static Particle of(final ParticleType type) {
        return ParticleFactory.create(type);
    }

    /**
     * Creates new particle from the given type and options.
     * @param type type of the particle
     * @param options options of the particle
     * @return particle created from the given type and options
     */
    public static Particle of(final ParticleType type, final @Nullable ParticleOptions options) {
        ParticleImpl particle = new ParticleImpl(type);
        particle.options = options;
        return particle;
    }

    /**
     * Creates new particle from the buffer.
     * @param buf buffer with particle id and options
     * @return particle created from the buffer
     */
    public static Particle fromBuffer(final FriendlyByteBuf buf) {
        ParticleType type = ParticleType.fromID(buf.readVarInt());
        return ParticleFactory.create(type, buf);
    }

    /**
     * @return options of the particle
     */
    public Optional<ParticleOptions> getOptions() {
        return Optional.ofNullable(options);
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeVarInt(type.getId());
        if (options != null)
            buf.write(options);
    }

    @Override
    public NBTCompound toNBT() {
        NBTCompound particle = new NBTCompound(Map.of("type", type.getName().getKey()));
        if (options == null) return particle;
        particle.putAll(options.toNBT());
        return particle;
    }

}
