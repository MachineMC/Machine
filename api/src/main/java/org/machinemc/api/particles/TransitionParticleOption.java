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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTFloat;
import org.machinemc.nbt.NBTList;

import java.awt.*;

/**
 * Particle options implementation for color transition dust particles.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransitionParticleOption implements ParticleOption {

    private static final Color DEFAULT_COLOR = Color.WHITE;

    private @Nullable Color from;
    private @Nullable Color to;
    private float scale = 1;

    @Override
    public void load(final NBTCompound compound) {
        final NBTList fromColor = compound.getList("fromColor");
        if (fromColor.size() < 3)
            from = DEFAULT_COLOR;
        else {
            final int[] rgb = new int[3];
            for (int i = 0; i < rgb.length; i++) {
                final Object value = fromColor.get(i).value();
                if (value instanceof Number c) rgb[i] = (int) (c.floatValue() * 255);
            }
            from = new Color(rgb[0], rgb[1], rgb[2]);
        }

        final NBTList toColor = compound.getList("toColor");
        if (toColor.size() < 3)
            to = DEFAULT_COLOR;
        else {
            final int[] rgb = new int[3];
            for (int i = 0; i < rgb.length; i++) {
                final Object value = toColor.get(i).value();
                if (value instanceof Number c) rgb[i] = (int) (c.floatValue() * 255);
            }
            to = new Color(rgb[0], rgb[1], rgb[2]);
        }

        if (compound.containsKey("scale") && compound.get("scale").tag() == NBT.Tag.FLOAT)
            scale = compound.get("scale").value();
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        final Color from = this.from != null ? this.from : DEFAULT_COLOR;
        final Color to = this.to != null ? this.to : DEFAULT_COLOR;
        compound.put("fromColor", new NBTList(
                new NBTFloat(from.getRed() / 255f),
                new NBTFloat(from.getGreen() / 255f),
                new NBTFloat(from.getBlue() / 255f)
        ));
        compound.put("toColor", new NBTList(
                new NBTFloat(to.getRed() / 255f),
                new NBTFloat(to.getGreen() / 255f),
                new NBTFloat(to.getBlue() / 255f)
        ));
        compound.put("scale", new NBTFloat(scale));
        return compound;
    }

    @Override
    public void write(final ServerBuffer buf) {
        final Color from = this.from != null ? this.from : DEFAULT_COLOR;
        final Color to = this.to != null ? this.to : DEFAULT_COLOR;
        buf.writeFloat(from.getRed() / 255f);
        buf.writeFloat(from.getGreen() / 255f);
        buf.writeFloat(from.getBlue() / 255f);
        buf.writeFloat(scale);
        buf.writeFloat(to.getRed() / 255f);
        buf.writeFloat(to.getGreen() / 255f);
        buf.writeFloat(to.getBlue() / 255f);
    }

}
