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
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;

import java.util.Map;

/**
 * Represents a particle object.
 * @param <O> particle options
 */
@Getter
@Setter
public class Particle<O extends ParticleOption> implements NBTSerializable, Writable {

    private final ParticleType<O> type;
    private O options;

    /**
     * Creates new particle instance from given NBT.
     * @see Particle#toNBT()
     * @param compound compound of the particle
     * @return particle created from the compound
     */
    public static @Nullable Particle<?> fromNBT(final NBTCompound compound) {
        if (!compound.containsKey("type") || compound.get("type").tag() != NBT.Tag.STRING)
            return null;
        final NamespacedKey name = NamespacedKey.minecraft(compound.get("type").value());
        final ParticleType<?> particleType = ParticleType.get(name);
        if (particleType == null) return null;
        final Particle<?> particle = particleType.create();
        particle.getOptions().load(compound);
        return particle;
    }

    public Particle(final ParticleType<O> type) {
        this.type = type;
        options = type.provider.create(type);
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound particle = new NBTCompound(Map.of("type", type.getName().getKey()));
        particle.putAll(options.toNBT());
        return particle;
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeVarInt(type.getId());
        buf.write(options);
    }

    @Override
    public String toString() {
        return type.getName().toString() + options.toNBT().toString();
    }
}
