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
import org.machinemc.api.inventory.Item;
import org.machinemc.api.inventory.ItemStack;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.Material;
import org.machinemc.nbt.NBT;
import org.machinemc.nbt.NBTCompound;

import java.util.Map;

/**
 * Particle options implementation for item particles.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemParticleOption implements ParticleOption {

    private static final Item DEFAULT_ITEM = new ItemStack(Material.STONE);

    private @Nullable Item item;

    @Override
    public void load(final NBTCompound compound) {
        final NBTCompound value;
        if (!compound.containsKey("value") || compound.get("value").tag() != NBT.Tag.COMPOUND)
            return;
        else
            value = (NBTCompound) compound.get("value");

        final String name;
        if (!value.containsKey("id") || value.get("id").tag() != NBT.Tag.STRING)
            return;
        else
            name = value.get("id").value();

        final Material material;
        try {
            material = Material.valueOf(name.replace("minecraft:", "").toUpperCase());
        } catch (Throwable throwable) {
            return;
        }

        final int amount;
        if (value.containsKey("Count") && value.get("Count").tag() == NBT.Tag.INT)
            amount = value.get("Count").value();
        else
            amount = 1;

        final NBTCompound tag;
        if (value.containsKey("tag") && value.get("tag").tag() == NBT.Tag.COMPOUND)
            tag = value.get("tag").value();
        else
            tag = new NBTCompound();

        item = new ItemStack(material);
        item.setAmount((byte) amount);
        item.setNbtCompound(tag);
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound();
        final Item item = this.item != null ? this.item : DEFAULT_ITEM;
        compound.put("id", item.getMaterial().getName().toString());
        compound.put("Count", (int) item.getAmount());
        compound.put("tag", item.getNbtCompound());
        return new NBTCompound(Map.of("value", compound));
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeSlot(item);
    }

}
