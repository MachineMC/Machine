package org.machinemc.api.world.particles;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.particles.options.BlockOptions;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a type (base visual) of a particle.
 */
@Getter
@RequiredArgsConstructor
public enum ParticleType {

    AMBIENT_ENTITY_EFFECT(NamespacedKey.minecraft("ambient_entity_effect"), 0, null),
    ANGRY_VILLAGER(NamespacedKey.minecraft("angry_villager"), 1, null),
    BLOCK(NamespacedKey.minecraft("block"), 2, BlockOptions.class);

    private final NamespacedKey name;
    private final int id;
    private final Class<? extends ParticleOptions> options;

    /**
     * Returns particle type from its numeric id.
     * @param id id of the particle type
     * @return particle type for given id
     */
    public static ParticleType fromID(final int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported particle type");
        return values()[id];
    }

    /**
     * Returns particle type of given name.
     * @param name name of the particle type
     * @return particle type with given name
     */
    public static @Nullable ParticleType getByName(final String name) {
        for (final ParticleType value : values()) {
            if (value.name().equalsIgnoreCase(name)) return value;
        }
        return null;
    }

}
