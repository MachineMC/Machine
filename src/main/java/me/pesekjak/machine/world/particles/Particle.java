package me.pesekjak.machine.world.particles;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.Optional;

public class Particle implements NBTSerializable {

    @Getter @Setter
    private ParticleType type;
    @Setter
    private ParticleOptions options;

    private Particle(ParticleType type) {
        this.type = type;
    }

    public static Particle of(ParticleType type) {
        return new Particle(type);
    }

    public static Particle of(ParticleType type, ParticleOptions options) {
        Particle particle = new Particle(type);
        particle.options = options;
        return particle;
    }

    public static Particle fromBuffer(FriendlyByteBuf buf) {
        ParticleType type = ParticleType.byId(buf.readVarInt());
        return type.getCreator().create(type, buf);
    }

    public Optional<ParticleOptions> getOptions() {
        return Optional.of(options);
    }

    public FriendlyByteBuf write(FriendlyByteBuf buf) {
        buf.writeVarInt(type.getID());
        if(options != null)
            options.write(buf);
        return buf;
    }

    @Override
    public NBTCompound toNBT() {
        NBTCompound particle = NBT.Compound(Map.of("type", NBT.String(type.getName().getKey())));
        if(options == null) return particle;
        return particle.plus(options.toNBT());
    }

}
