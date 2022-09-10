package me.pesekjak.machine.world.particles;

import me.pesekjak.machine.utils.FriendlyByteBuf;

@FunctionalInterface
public interface ParticleCreator {

    ParticleCreator empty = (type, buf) -> Particle.of(type);

    Particle create(ParticleType type, FriendlyByteBuf buf);

}
