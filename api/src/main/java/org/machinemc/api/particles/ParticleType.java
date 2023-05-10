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
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.api.particles;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a type (base visual) of a particle.
 * @param <O> particle options
 */
public final class ParticleType<O extends ParticleOption> {

    public static final ParticleType<ParticleOption> AMBIENT_ENTITY_EFFECT;
    public static final ParticleType<ParticleOption> ANGRY_VILLAGER;
    public static final ParticleType<BlockParticleOption> BLOCK;
    public static final ParticleType<DustParticleOption> DUST;
    public static final ParticleType<BlockParticleOption> FALLING_DUST;
    public static final ParticleType<ItemParticleOption> ITEM;
    public static final ParticleType<VibrationParticleOption> VIBRATION;

    private static final Map<NamespacedKey, ParticleType<?>> REGISTRY = new HashMap<>();

    @Getter
    private final NamespacedKey name;
    @Getter
    private final int id;
    protected final ParticleOptionProvider<O> provider;

    static {
        AMBIENT_ENTITY_EFFECT = new ParticleType<>(NamespacedKey.minecraft("ambient_entity_effect"), 0);
        ANGRY_VILLAGER = new ParticleType<>(NamespacedKey.minecraft("angry_villager"), 1);
        BLOCK = new ParticleType<>(
                NamespacedKey.minecraft("block"),
                1,
                type -> new BlockParticleOption());
        DUST = new ParticleType<>(
                NamespacedKey.minecraft("dust"),
                14,
                type -> new DustParticleOption());
        FALLING_DUST = new ParticleType<>(
                NamespacedKey.minecraft("falling_dust"),
                25,
                type -> new BlockParticleOption());
        ITEM = new ParticleType<>(
                NamespacedKey.minecraft("item"),
                42,
                type -> new ItemParticleOption());
        VIBRATION = new ParticleType<>(
                NamespacedKey.minecraft("vibration"),
                43,
                type -> new VibrationParticleOption());
    }

    /**
     * Returns particle type with given namespaced key.
     * @param name name of the particle type
     * @return particle type with given name
     */
    public static @Nullable ParticleType<?> get(final NamespacedKey name) {
        return REGISTRY.get(name);
    }

    private ParticleType(final NamespacedKey name,
                         final int id,
                         final ParticleOptionProvider<O> provider) {
        REGISTRY.put(name, this);
        this.name = name;
        this.id = id;
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    private ParticleType(final NamespacedKey name, final int id) {
        this(name, id, type -> (O) new ParticleOption.SimpleOptions());
    }

    /**
     * Create new particle from the type.
     * @return new particle of this type
     */
    public Particle<O> create() {
        return new Particle<>(this);
    }

}
