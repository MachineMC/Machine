package me.pesekjak.machine.world.particles;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.utils.Writable;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.Optional;

/**
 * Represents a playable particle.
 */
public class ParticleImpl implements Particle {

    @Getter @Setter
    private ParticleType type;
    @Setter
    private ParticleOptions options;

    private ParticleImpl(ParticleType type) {
        this.type = type;
    }

    /**
     * Creates new particle from the given type.
     * @param type type of the particle
     * @return particle created from the given type
     */
    public static ParticleImpl of(ParticleType type) {
        return new ParticleImpl(type);
    }

    /**
     * Creates new particle from the given type and options.
     * @param type type of the particle
     * @param options options of the particle
     * @return particle created from the given type and options
     */
    public static ParticleImpl of(ParticleType type, ParticleOptions options) {
        ParticleImpl particle = new ParticleImpl(type);
        particle.options = options;
        return particle;
    }

    /**
     * Creates new particle from the buffer
     * @param buf buffer with particle id and options
     * @return particle created from the buffer
     */
    public static ParticleImpl fromBuffer(FriendlyByteBuf buf) {
        ParticleType type = ParticleType.fromID(buf.readVarInt());
        return ParticleFactory.create(type, buf);
    }

    /**
     * @return options of the particle
     */
    public @NotNull Optional<ParticleOptions> getOptions() {
        return Optional.of(options);
    }

    /**
     * Writes the particle to the buffer.
     * @param buf buffer to write to
     */
    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeVarInt(type.getId());
        if(options != null)
            buf.write(options);
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        NBTCompound particle = NBT.Compound(Map.of("type", NBT.String(type.getName().key())));
        if(options == null) return particle;
        return particle.plus(options.toNBT());
    }

}
