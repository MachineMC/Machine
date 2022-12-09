package me.pesekjak.machine.world.particles;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of the particle.
 */
@ToString
public class ParticleImpl implements Particle {

    @Getter @Setter
    private @NotNull ParticleType type;
    @Setter
    private @Nullable ParticleOptions options;

    private ParticleImpl(@NotNull ParticleType type) {
        this.type = type;
    }

    /**
     * Creates new particle from the given type.
     * @param type type of the particle
     * @return particle created from the given type
     */
    public static @NotNull ParticleImpl of(@NotNull ParticleType type) {
        return ParticleFactory.create(type);
    }

    /**
     * Creates new particle from the given type and options.
     * @param type type of the particle
     * @param options options of the particle
     * @return particle created from the given type and options
     */
    public static @NotNull ParticleImpl of(@NotNull ParticleType type, @Nullable ParticleOptions options) {
        ParticleImpl particle = new ParticleImpl(type);
        particle.options = options;
        return particle;
    }

    /**
     * Creates new particle from the buffer.
     * @param buf buffer with particle id and options
     * @return particle created from the buffer
     */
    public static @NotNull ParticleImpl fromBuffer(@NotNull FriendlyByteBuf buf) {
        ParticleType type = ParticleType.fromID(buf.readVarInt());
        return ParticleFactory.create(type, buf);
    }

    /**
     * @return options of the particle
     */
    public @NotNull Optional<ParticleOptions> getOptions() {
        return Optional.ofNullable(options);
    }

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
