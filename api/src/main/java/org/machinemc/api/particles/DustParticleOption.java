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
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTFloat;
import org.machinemc.nbt.NBTList;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;

/**
 * Particle options implementation for dust particles.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DustParticleOption implements ParticleOption {

    private static final Colour DEFAULT_COLOR = ChatColor.WHITE;

    private @Nullable Colour color;
    private float scale = 1;

    @Override
    public void load(final NBTCompound compound) {
        final NBTList colors = compound.getList("color");
        if (colors.size() < 3)
            color = DEFAULT_COLOR;
        else {
            final int[] rgb = new int[3];
            for (int i = 0; i < rgb.length; i++) {
                final Object value = colors.get(i).value();
                if (value instanceof Number c) rgb[i] = (int) (c.floatValue() * 255);
            }
            color = new HexColor(rgb[0], rgb[1], rgb[2]);
        }

        if (compound.containsKey("scale") && compound.get("scale").tag() == NBT.Tag.FLOAT)
            scale = compound.get("scale").value();
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        final Colour color = this.color != null ? this.color : DEFAULT_COLOR;
        compound.put("color", new NBTList(
                new NBTFloat(color.getRed() / 255f),
                new NBTFloat(color.getGreen() / 255f),
                new NBTFloat(color.getBlue() / 255f)
        ));
        compound.put("scale", new NBTFloat(scale));
        return compound;
    }

    @Override
    public void write(final ServerBuffer buf) {
        final Colour color = this.color != null ? this.color : DEFAULT_COLOR;
        buf.writeFloat(color.getRed() / 255f);
        buf.writeFloat(color.getGreen() / 255f);
        buf.writeFloat(color.getBlue() / 255f);
        buf.writeFloat(scale);
    }

}
