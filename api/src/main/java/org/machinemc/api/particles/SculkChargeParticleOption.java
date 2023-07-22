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

import lombok.*;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTFloat;

import java.util.Objects;

/**
 * Particle options implementation for sculk charge particles.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SculkChargeParticleOption implements ParticleOption {

    private float roll = 1;

    @Override
    public void load(final NBTCompound compound) {
        Objects.requireNonNull(compound, "Source compound can not be null");
        if (compound.containsKey("roll") && compound.get("roll").tag() == NBT.Tag.FLOAT)
            roll = compound.get("roll").value();
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        compound.put("roll", new NBTFloat(roll));
        return compound;
    }

    @Override
    public void write(final ServerBuffer buf) {
        Objects.requireNonNull(buf);
        buf.writeFloat(roll);
    }

}
