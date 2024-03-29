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
package org.machinemc.api.inventory;

import lombok.*;
import org.machinemc.api.world.Material;
import org.machinemc.nbt.NBTCompound;

import java.util.Optional;

import java.util.Objects;

/**
 * Default item implementation.
 */
@Data
class ItemStack implements Item {

    private static final Material[] REGISTRY;

    private Material material;
    private byte amount = 1;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private NBTCompound nbtCompound = new NBTCompound();

    static {
        final Material[] materials = Material.values();
        REGISTRY = new Material[materials.length];
        for (final Material value : materials) {
            if (value.getID() >= 0) REGISTRY[value.getID()] = value;
        }
    }

    /**
     * Returns a material from id (mapped by vanilla server reports).
     * @param id id of the material
     * @return material with the id
     */
    @Synchronized
    public static Optional<Material> getMaterial(final int id) {
        if (id == -1) return Optional.empty();
        if (REGISTRY.length <= id) return Optional.empty();
        return Optional.ofNullable(REGISTRY[id]);
    }

    /**
     * Returns id of the material.
     * @param material material to get id from
     * @return id of the material
     */
    public static int getID(final Material material) {
        return Objects.requireNonNull(material, "Material can not be null").getID();
    }

    ItemStack(final Material material) {
        Objects.requireNonNull(material, "Material can not be null");
        if (material.getID() == -1)
            throw new IllegalStateException("Material " + material + " can't have item form");
        this.material = material;
    }

    ItemStack(final Material material, final byte amount) {
        this(material);
        this.amount = amount;
    }

    @Override
    public ItemStack withMaterial(final Material material) {
        Objects.requireNonNull(material, "Material can not be null");
        final ItemStack itemStack = clone();
        itemStack.setMaterial(material);
        return itemStack;
    }

    @Override
    public ItemStack withAmount(final byte amount) {
        final ItemStack itemStack = clone();
        itemStack.setAmount(amount);
        return itemStack;
    }

    @Override
    public NBTCompound getNBTCompound() {
        return nbtCompound;
    }

    @Override
    public void setNBTCompound(final NBTCompound compound) {
        this.nbtCompound = Objects.requireNonNull(compound, "NBTCompound can not be null");
    }

    @Override
    public ItemStack withNBTCompound(final NBTCompound compound) {
        Objects.requireNonNull(compound, "NBTCompound can not be null");
        final ItemStack itemStack = clone();
        itemStack.setNBTCompound(compound);
        return itemStack;
    }

    @Override
    public Material getType() {
        return material;
    }

    @Override
    public void setType(final Material material) {
        this.material = Objects.requireNonNull(material, "Material can not be null");
    }

    @Override
    public ItemStack withType(final Material type) {
        Objects.requireNonNull(material, "Material can not be null");
        final ItemStack itemStack = clone();
        itemStack.setMaterial(type);
        return itemStack;
    }

    @Override
    public void removeNBT() {
        nbtCompound = new NBTCompound();
    }

    @Override
    public void add(final byte amount) {
        this.amount += amount;
    }

    @Override
    public void subtract(final byte amount) {
        this.amount -= amount;
    }

    @Override
    public ItemStack single() {
        final ItemStack single = clone();
        single.amount = 1;
        return single;
    }

    @Override
    public ItemStack clone() {
        final ItemStack clone = new ItemStack(material);
        clone.setAmount(amount);
        clone.setNBTCompound(nbtCompound.clone());
        return clone;
    }

    @Override
    public String toString() {
        return amount + "x" + material + nbtCompound;
    }

}
