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
 * Particle options implementation for color transition dust particles.
 */
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransitionParticleOption implements ParticleOption {

    private static final Colour DEFAULT_COLOR = ChatColor.WHITE;

    private @Nullable Colour from;
    private @Nullable Colour to;
    @Getter
    private float scale = 1;

    @Override
    public void load(final NBTCompound compound) {
        Objects.requireNonNull(compound, "Source compound can not be null");
        final List<Object> fromColor = compound.getValue("fromColor");
        if (fromColor.size() < 3)
            from = DEFAULT_COLOR;
        else {
            final int[] rgb = new int[3];
            for (int i = 0; i < rgb.length; i++) {
                final Object value = fromColor.get(i);
                if (value instanceof Number c) rgb[i] = (int) (c.floatValue() * 255);
            }
            from = new HexColor(rgb[0], rgb[1], rgb[2]);
        }

        final List<Object> toColor = compound.getValue("toColor");
        if (toColor.size() < 3)
            to = DEFAULT_COLOR;
        else {
            final int[] rgb = new int[3];
            for (int i = 0; i < rgb.length; i++) {
                final Object value = toColor.get(i);
                if (value instanceof Number c) rgb[i] = (int) (c.floatValue() * 255);
            }
            to = new HexColor(rgb[0], rgb[1], rgb[2]);
        }

        if (compound.containsKey("scale") && compound.getNBT("scale").tag() == NBT.Tag.FLOAT)
            scale = compound.getValue("scale");
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        final Colour from = this.from != null ? this.from : DEFAULT_COLOR;
        final Colour to = this.to != null ? this.to : DEFAULT_COLOR;
        compound.set("fromColor", new NBTList(
                new NBTFloat(from.getRed() / 255f),
                new NBTFloat(from.getGreen() / 255f),
                new NBTFloat(from.getBlue() / 255f)
        ));
        compound.set("toColor", new NBTList(
                new NBTFloat(to.getRed() / 255f),
                new NBTFloat(to.getGreen() / 255f),
                new NBTFloat(to.getBlue() / 255f)
        ));
        compound.set("scale", new NBTFloat(scale));
        return compound;
    }

    @Override
    public void write(final ServerBuffer buf) {
        Objects.requireNonNull(buf);
        final Colour from = this.from != null ? this.from : DEFAULT_COLOR;
        final Colour to = this.to != null ? this.to : DEFAULT_COLOR;
        buf.writeFloat(from.getRed() / 255f);
        buf.writeFloat(from.getGreen() / 255f);
        buf.writeFloat(from.getBlue() / 255f);
        buf.writeFloat(scale);
        buf.writeFloat(to.getRed() / 255f);
        buf.writeFloat(to.getGreen() / 255f);
        buf.writeFloat(to.getBlue() / 255f);
    }

    /**
     * @return the 'from' color of the transition particles
     */
    public Optional<Colour> getFrom() {
        return Optional.ofNullable(from);
    }

    /**
     * @return the 'to' color of the transition particles
     */
    public Optional<Colour> getTo() {
        return Optional.ofNullable(to);
    }

}
