package org.machinemc.server.inventory;

import lombok.Data;
import lombok.Synchronized;
import org.machinemc.api.inventory.Item;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.Material;

/**
 * Default item implementation.
 */
@Data
public class ItemStack implements Item {

    private static final Material[] REGISTRY;

    private Material material;
    private byte amount = 1;
    private NBTCompound nbtCompound = new NBTCompound();

    static {
        final Material[] materials = Material.values();
        REGISTRY = new Material[materials.length];
        for (final Material value : materials) {
            if (value.getId() >= 0) REGISTRY[value.getId()] = value;
        }
    }

    /**
     * Returns a material from id (mapped by vanilla server reports).
     * @param id id of the material
     * @return material with the id
     */
    @Synchronized
    public static @Nullable Material getMaterial(final int id) {
        if (id == -1) return null;
        if (REGISTRY.length <= id) return null;
        return REGISTRY[id];
    }

    /**
     * Returns id of the material.
     * @param material material to get id from
     * @return id of the material
     */
    public static int getId(final Material material) {
        return material.getId();
    }

    public ItemStack(final Material material) {
        if (material.getId() == -1)
            throw new IllegalStateException("Material " + material + " can't have item form");
        this.material = material;
    }

    public ItemStack(final Material material, final byte amount) {
        this(material);
        this.amount = amount;
    }

    @Override
    public ItemStack withMaterial(final Material material) {
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
    public ItemStack withNbtCompound(final NBTCompound compound) {
        final ItemStack itemStack = clone();
        itemStack.setNbtCompound(compound);
        return itemStack;
    }

    @Override
    public Material getType() {
        return material;
    }

    @Override
    public void setType(final Material material) {
        this.material = material;
    }

    @Override
    public ItemStack withType(final Material type) {
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
        try {
            final ItemStack itemStack = (ItemStack) super.clone();
            itemStack.setNbtCompound(new FriendlyByteBuf().writeNBT(nbtCompound).readNBT());
            return itemStack;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return amount + "x" + material + nbtCompound;
    }

}
