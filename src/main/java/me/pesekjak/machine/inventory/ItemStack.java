package me.pesekjak.machine.inventory;

import lombok.Data;
import me.pesekjak.machine.world.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;

import java.io.StringReader;

/**
 * Default item implementation.
 */
@Data
public class ItemStack implements Item {

    private final static Material @NotNull [] REGISTRY;

    private @NotNull Material material;
    private byte amount = 1;
    private @NotNull NBTCompound nbtCompound = new NBTCompound();

    static {
        Material[] materials = Material.values();
        REGISTRY = new Material[materials.length];
        for (Material value : materials) {
            if(value.getId() >= 0) REGISTRY[value.getId()] = value;
        }
    }

    /**
     * Returns a material from id (mapped by vanilla server reports).
     * @param id id of the material
     * @return material with the id
     */
    @Contract(pure = true)
    public static @Nullable Material getMaterial(int id) {
        if(id == -1) return null;
        if(REGISTRY.length <= id) return null;
        return REGISTRY[id];
    }

    /**
     * Returns id of the material.
     * @param material material to get id from
     * @return id of the material
     */
    @Contract(pure = true)
    public static int getId(@NotNull Material material) {
        return material.getId();
    }

    public ItemStack(@NotNull Material material) {
        if(material.getId() == -1)
            throw new IllegalStateException("Material " + material + " can't have item form");
        this.material = material;
    }

    public ItemStack(@NotNull Material material, byte amount) {
        this(material);
        this.amount = amount;
    }

    @Override
    public @NotNull ItemStack withMaterial(@NotNull Material material) {
        final ItemStack itemStack = clone();
        itemStack.setMaterial(material);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack withAmount(byte amount) {
        final ItemStack itemStack = clone();
        itemStack.setAmount(amount);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack withNbtCompound(NBTCompound compound) {
        final ItemStack itemStack = clone();
        itemStack.setNbtCompound(compound);
        return itemStack;
    }

    @Override
    public @NotNull Material getType() {
        return material;
    }

    @Override
    public void setType(@NotNull Material material) {
        this.material = material;
    }

    @Override
    public @NotNull ItemStack withType(Material type) {
        final ItemStack itemStack = clone();
        itemStack.setMaterial(type);
        return itemStack;
    }

    @Override
    public void writeNBT(@NotNull String nbtCompound) throws NBTException {
        this.nbtCompound = (NBTCompound) new SNBTParser(new StringReader(nbtCompound)).parse();
    }

    @Override
    public void removeNBT() {
        nbtCompound = new NBTCompound();
    }

    @Override
    public void add(byte amount) {
        this.amount += amount;
    }

    @Override
    public void subtract(byte amount) {
        this.amount -= amount;
    }

    @Override
    public @NotNull ItemStack single() {
        ItemStack single = clone();
        single.amount = 1;
        return single;
    }

    @Override
    public ItemStack clone() {
        try {
            ItemStack itemStack = (ItemStack) super.clone();
            itemStack.nbtCompound = nbtCompound.toMutableCompound().toCompound();
            return itemStack;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return amount + "x" + material + nbtCompound.toSNBT();
    }

}
