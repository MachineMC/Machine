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
import org.machinemc.api.inventory.Item;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.Material;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Particle options implementation for item particles.
 */
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemParticleOption implements ParticleOption {

    private static final Item DEFAULT_ITEM = Item.of(Material.STONE);

    private @Nullable Item item;

    @Override
    public void load(final NBTCompound compound) {
        Objects.requireNonNull(compound, "Source compound can not be null");
        final NBTCompound value;
        if (!compound.containsKey("value") || compound.getNBT("value").tag() != NBT.Tag.COMPOUND)
            return;
        else
            value = compound.getNBT("value");

        final String name;
        if (!value.containsKey("id") || value.getNBT("id").tag() != NBT.Tag.STRING)
            return;
        else
            name = value.getValue("id");

        final Material material;
        try {
            material = Material.valueOf(name.replace("minecraft:", "").toUpperCase());
        } catch (Throwable throwable) {
            return;
        }

        final int amount;
        if (value.containsKey("Count") && value.getNBT("Count").tag() == NBT.Tag.INT)
            amount = value.getValue("Count");
        else
            amount = 1;

        final NBTCompound tag;
        if (value.containsKey("tag") && value.getNBT("tag").tag() == NBT.Tag.COMPOUND)
            tag = value.getValue("tag");
        else
            tag = new NBTCompound();

        item = Item.of(material);
        item.setAmount((byte) amount);
        item.setNBTCompound(tag);
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        final Item item = this.item != null ? this.item : DEFAULT_ITEM;
        compound.set("id", item.getMaterial().getName().toString());
        compound.set("Count", (int) item.getAmount());
        compound.set("tag", item.getNBTCompound());
        return new NBTCompound(Map.of("value", compound));
    }

    @Override
    public void write(final ServerBuffer buf) {
        Objects.requireNonNull(buf);
        buf.writeSlot(item);
    }

    /**
     * @return item of the item particles
     */
    public Optional<Item> getItem() {
        return Optional.ofNullable(item);
    }

}
