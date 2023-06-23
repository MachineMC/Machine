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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.jetbrains.annotations.Contract;
import org.machinemc.api.world.Material;

/**
 * Represents an item in inventory.
 */
public interface Item extends Writable, Cloneable {

    /**
     * Creates new item with given amount and material.
     * @param material material of the item
     * @param amount amount of the item
     * @return new item
     * @throws IllegalStateException if the material can't have item form
     */
    static Item of(Material material, byte amount) {
        return new ItemStack(material, amount);
    }

    /**
     * Creates new item with given amount and material.
     * @param material material of the item
     * @param amount amount of the item
     * @return new item
     * @throws IllegalStateException if the material can't have item form
     */
    static Item of(Material material, int amount) {
        return of(material, (byte) amount);
    }

    /**
     * Creates new item with given material.
     * @param material material of the item
     * @return new item
     * @throws IllegalStateException if the material can't have item form
     */
    static Item of(Material material) {
        return new ItemStack(material);
    }

    /**
     * Returns a material from id (mapped by vanilla server reports).
     * @param id id of the material
     * @return material with the id
     */
    static @Nullable Material getMaterial(final int id) {
        return ItemStack.getMaterial(id);
    }

    /**
     * @return material of the item
     */
    @Contract(pure = true)
    Material getMaterial();

    /**
     * @param material new material
     */
    void setMaterial(Material material);

    /**
     * Creates copy of this item with new material.
     * @param material new material
     * @return new item
     */
    @Contract(pure = true)
    Item withMaterial(Material material);

    /**
     * @return amount of the item
     */
    byte getAmount();

    /**
     * @param amount new amount of the item
     */
    void setAmount(byte amount);

    /**
     * Creates copy of this item with different amount.
     * @param amount new amount
     * @return new item
     */
    @Contract(pure = true)
    Item withAmount(byte amount);

    /**
     * @return NBT Compound of the item
     */
    NBTCompound getNBTCompound();

    /**
     * @param compound new nbt compound of the item
     */
    void setNBTCompound(NBTCompound compound);

    /**
     * Creates copy of this item with new nbt compound.
     * @param compound new compound
     * @return new item
     */
    @Contract(pure = true)
    Item withNBTCompound(NBTCompound compound);

    /**
     * @return material of the item
     */
    @Contract(pure = true)
    Material getType();

    /**
     * @param material new material
     */
    void setType(Material material);

    /**
     * Creates copy of this item with new material.
     * @param type new material
     * @return new item
     */
    @Contract(pure = true)
    Item withType(Material type);

    /**
     * Clears the NBT Compound of the item.
     */
    void removeNBT();

    /**
     * Adds given amount to the amount of the item.
     * @param amount amount to add
     */
    void add(byte amount);

    /**
     * Adds 1 to the amount of the item.
     */
    default void add() {
        add((byte) 1);
    }

    /**
     * Subtracts given amount from the amount of the item.
     * @param amount amount to subtract
     */
    void subtract(byte amount);

    /**
     * Subtracts 1 from the amount of the item.
     */
    default void subtract() {
        subtract((byte) 1);
    }

    /**
     * @return copy of this item with amount of 1
     */
    @Contract(pure = true)
    Item single();

    /**
     * Writes the item in to a buffer.
     * @param buf buffer to write into
     */
    @ApiStatus.NonExtendable
    default void write(ServerBuffer buf) {
        buf.writeSlot(this);
    }

}
