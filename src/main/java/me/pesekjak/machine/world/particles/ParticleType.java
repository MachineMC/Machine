package me.pesekjak.machine.world.particles;

import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.particles.options.BlockOptions;

import java.util.HashMap;

/**
 * All ParticleTypes supported by the server.
 */
@Getter
public enum ParticleType {

    AMBIENT_ENTITY_EFFECT(NamespacedKey.minecraft("ambient_entity_effect"), 0, ParticleCreator.empty),
    ANGRY_VILLAGER(NamespacedKey.minecraft("angry_villager"), 1, ParticleCreator.empty),
    BLOCK(NamespacedKey.minecraft("block"), 2, ((type, buf) -> Particle.of(type, new BlockOptions(buf))));

    private final NamespacedKey name;
    private final int ID;
    private final ParticleCreator creator;

    private static final HashMap<Integer, ParticleType> ID_MAP = new HashMap<>();

    ParticleType(NamespacedKey name, int ID, ParticleCreator creator) {
        this.name = name;
        this.ID = ID;
        this.creator = creator;
    }

    /**
     * Returns the ParticleType by ID.
     * @param ID ID of the ParticleType
     * @return ParticleType with the provided id
     */
    public static ParticleType byId(int ID) {
        return ID_MAP.get(ID);
    }

    /**
     * Returns the ParticleType by name.
     * @param name name of the ParticleType
     * @return ParticleType with provided name
     */
    public static ParticleType getParticleType(NamespacedKey name) {
        for(ParticleType particleType : values()) {
            if(particleType.name.equals(name)) return particleType;
        }
        return null;
    }

}
