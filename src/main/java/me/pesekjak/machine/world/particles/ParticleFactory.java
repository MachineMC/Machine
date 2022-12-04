package me.pesekjak.machine.world.particles;

import lombok.experimental.UtilityClass;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.particles.options.BlockOptionsImpl;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ParticleFactory {

    private final Map<ParticleType, ParticleCreator> CREATOR_MAP = new HashMap<>();

    static {
        CREATOR_MAP.put(ParticleType.AMBIENT_ENTITY_EFFECT, ParticleCreator.empty);
        CREATOR_MAP.put(ParticleType.ANGRY_VILLAGER, ParticleCreator.empty);
        CREATOR_MAP.put(ParticleType.BLOCK, ((type, buf) -> ParticleImpl.of(type, new BlockOptionsImpl(buf))));
    }

    public static ParticleImpl create(ParticleType type, ServerBuffer buf) {
        return CREATOR_MAP.get(type).create(type, buf);
    }

}
