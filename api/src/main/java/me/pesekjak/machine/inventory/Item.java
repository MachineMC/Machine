package me.pesekjak.machine.inventory;

import me.pesekjak.machine.utils.Writable;
import me.pesekjak.machine.world.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;

/**
 * Represents an item in inventory.
 */
public interface Item extends Writable, Cloneable {

    /**
     * @return material of the item
     */
    @Contract(pure = true)
    @NotNull Material getMaterial();

    /**
     * @param material new material
     */
    void setMaterial(@NotNull Material material);

    /**
     * Creates copy of this item with new material
     * @param material new material
     * @return new item
     */
    @Contract(pure = true)
    @NotNull Item withMaterial(@NotNull Material material);

    /**
     * @return amount of the item
     */
    byte getAmount();

    /**
     * @param amount new amount of the item
     */
    void setAmount(byte amount);

    /**
     * Creates copy of this item with different amount
     * @param amount new amount
     * @return new item
     */
    @Contract(pure = true)
    @NotNull Item withAmount(byte amount);

    /**
     * @return NBT Compound of the item
     */
    @NotNull NBTCompound getNbtCompound();

    /**
     * @param compound new nbt compound of the item
     */
    void setNbtCompound(@NotNull NBTCompound compound);

    /**
     * Creates copy of this item with new nbt compound
     * @param compound new compound
     * @return new item
     */
    @Contract(pure = true)
    @NotNull Item withNbtCompound(NBTCompound compound);

    /**
     * @return material of the item
     */
    @Contract(pure = true)
    @NotNull Material getType();

    /**
     * @param material new material
     */
    void setType(@NotNull Material material);

    /**
     * Creates copy of this item with new material
     * @param type new material
     * @return new item
     */
    @Contract(pure = true)
    @NotNull Item withType(Material type);

    /**
     * Writes additional nbt string to the item's nbt compound.
     * @param nbtCompound nbt string to write
     * @throws NBTException if string contains malformed NBT format
     */
    void writeNBT(@NotNull String nbtCompound) throws NBTException;

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
    @NotNull Item single();

    /**
     * @return serialized item
     */
    byte @NotNull [] serialize();

}
