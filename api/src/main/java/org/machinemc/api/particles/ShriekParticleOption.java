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
import org.machinemc.nbt.NBTInt;

/**
 * Particle options implementation for shriek particles.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShriekParticleOption implements ParticleOption {

    private int delay = 0;

    @Override
    public void load(final NBTCompound compound) {
        if (compound.containsKey("delay") && compound.get("delay").tag() == NBT.Tag.INT)
            delay = compound.get("delay").value();
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        compound.put("delay", new NBTInt(delay));
        return compound;
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeVarInt(delay);
    }

}
