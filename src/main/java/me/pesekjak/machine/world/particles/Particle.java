package me.pesekjak.machine.world.particles;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.Writable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.Optional;

/**
 * Represents playable particle, use {@link Particle#of(ParticleType)} or
 * {@link Particle#of(ParticleType, ParticleOptions)} to create new one.
 */
public class Particle implements NBTSerializable, Writable {

    @Getter @Setter
    private ParticleType type;
    @Setter
    private ParticleOptions options;

    private Particle(ParticleType type) {
        this.type = type;
    }

    /**
     * Creates new particle from the given type.
     * @param type type of the particle
     * @return particle created from the given type
     */
    public static Particle of(ParticleType type) {
        return new Particle(type);
    }

    /**
     * Creates new particle from the given type and options.
     * @param type type of the particle
     * @param options options of the particle
     * @return particle created from the given type and options
     */
    public static Particle of(ParticleType type, ParticleOptions options) {
        Particle particle = new Particle(type);
        particle.options = options;
        return particle;
    }

    /**
     * Creates new particle from the buffer
     * @param buf buffer with particle id and options
     * @return particle created from the buffer
     */
    public static Particle fromBuffer(FriendlyByteBuf buf) {
        ParticleType type = ParticleType.byId(buf.readVarInt());
        return type.getCreator().create(type, buf);
    }

    /**
     * @return options of the particle
     */
    public Optional<ParticleOptions> getOptions() {
        return Optional.of(options);
    }

    /**
     * Writes the particle to the buffer.
     * @param buf buffer to write to
     * @return updated buffer
     */
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(type.getID());
        if(options != null)
            buf.write(options);
    }

    @Override
    public NBTCompound toNBT() {
        NBTCompound particle = NBT.Compound(Map.of("type", NBT.String(type.getName().getKey())));
        if(options == null) return particle;
        return particle.plus(options.toNBT());
    }

}
