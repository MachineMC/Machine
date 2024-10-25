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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.Material;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Particle options implementation for block particles.
 */
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BlockParticleOption implements ParticleOption {

    private static final BlockData DEFAULT_STATE = Material.STONE.createBlockData();

    private @Nullable BlockData blockData;

    @Override
    public void load(final NBTCompound compound) {
        Objects.requireNonNull(compound, "Source compound can not be null");
        final NBTCompound value;
        if (!compound.containsKey("value") || compound.getNBT("value").tag() != NBT.Tag.COMPOUND)
            return;
        else
            value = compound.getNBT("value");

        final String name;
        if (!value.containsKey("Name") || value.getNBT("Name").tag() != NBT.Tag.STRING)
            return;
        else
            name = value.getValue("Name");

        final NBTCompound properties;
        if (!value.containsKey("Properties") || value.getNBT("Properties").tag() != NBT.Tag.COMPOUND)
            properties = new NBTCompound();
        else
            properties = value.getValue("Properties");

        final StringBuilder builder = new StringBuilder();
        builder.append(name).append('[');
        final int size = properties.size();
        int i = 0;
        for (final Map.Entry<String, NBT<?>> entry : properties) {
            i++;
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            if (i == size) break;
            builder.append(", ");
        }
        builder.append(']');

        setBlockData(BlockData.parse(builder.toString()).orElse(null));
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        final BlockData state = blockData != null ? blockData : DEFAULT_STATE;
        final Map<String, String> properties = state.getDataMap();
        compound.set("Name", state.getMaterial().getName().toString());
        if (properties.isEmpty()) return new NBTCompound(Map.of("value", compound));
        final NBTCompound propertiesCompound = new NBTCompound();
        for (final Map.Entry<String, String> property : properties.entrySet())
            propertiesCompound.set(property.getKey(), property.getValue());
        compound.set("Properties", propertiesCompound);
        return new NBTCompound(Map.of("value", compound));
    }

    @Override
    public void write(final ServerBuffer buf) {
        Objects.requireNonNull(buf);
        buf.writeVarInt(blockData != null ? blockData.getID() : DEFAULT_STATE.getID());
    }

    /**
     * @return block data of the block particles
     */
    public Optional<BlockData> getBlockData() {
        return Optional.ofNullable(blockData);
    }

}
