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

import java.util.List;
import java.util.Optional;

import java.util.Objects;

/**
 * Particle options implementation for dust particles.
 */

@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DustParticleOption implements ParticleOption {

    private static final Colour DEFAULT_COLOR = ChatColor.WHITE;

    private @Nullable Colour color;
    @Getter
    private float scale = 1;

    @Override
    public void load(final NBTCompound compound) {
        Objects.requireNonNull(compound, "Source compound can not be null");
        final List<Object> colors = compound.getValue("color");
        if (colors.size() < 3)
            color = DEFAULT_COLOR;
        else {
            final int[] rgb = new int[3];
            for (int i = 0; i < rgb.length; i++) {
                final Object value = colors.get(i);
                if (value instanceof Number c) rgb[i] = (int) (c.floatValue() * 255);
            }
            color = new HexColor(rgb[0], rgb[1], rgb[2]);
        }

        if (compound.containsKey("scale") && compound.getNBT("scale").tag() == NBT.Tag.FLOAT)
            scale = compound.getValue("scale");
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        final Colour color = this.color != null ? this.color : DEFAULT_COLOR;
        compound.set("color", new NBTList(
                new NBTFloat(color.getRed() / 255f),
                new NBTFloat(color.getGreen() / 255f),
                new NBTFloat(color.getBlue() / 255f)
        ));
        compound.set("scale", new NBTFloat(scale));
        return compound;
    }

    @Override
    public void write(final ServerBuffer buf) {
        Objects.requireNonNull(buf);
        final Colour color = this.color != null ? this.color : DEFAULT_COLOR;
        buf.writeFloat(color.getRed() / 255f);
        buf.writeFloat(color.getGreen() / 255f);
        buf.writeFloat(color.getBlue() / 255f);
        buf.writeFloat(scale);
    }

    /**
     * @return color of the dust particles
     */
    public Optional<Colour> getColor() {
        return Optional.ofNullable(color);
    }

}
