/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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
