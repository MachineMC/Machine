package me.pesekjak.machine.inventory;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;

import java.io.StringReader;

/**
 * Represents an item in inventory.
 */
public class ItemStack implements Item {

    private final static Material[] REGISTRY;

    @Getter @Setter
    private Material material;
    @Getter @Setter
    private byte amount = 1;
    @Getter @Setter
    private NBTCompound nbtCompound = new NBTCompound();

    static {
        Material[] materials = Material.values();
        REGISTRY = new Material[materials.length];
        for (Material value : materials) {
            if(value.getId() >= 0) REGISTRY[value.getId()] = value;
        }
    }

    /**
     * Returns a material from id (mapped by vanilla server reports)
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
     * Returns id of the material
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

    public ItemStack(Material material, byte amount) {
        this(material);
        this.amount = amount;
    }

    @Override
    public @NotNull Item withMaterial(@NotNull Material material) {
        final ItemStack itemStack = clone();
        itemStack.setMaterial(material);
        return itemStack;
    }

    @Override
    public @NotNull Item withAmount(byte amount) {
        final ItemStack itemStack = clone();
        itemStack.setAmount(amount);
        return itemStack;
    }

    @Override
    public @NotNull Item withNbtCompound(NBTCompound compound) {
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
    public @NotNull Item withType(Material type) {
        final ItemStack itemStack = clone();
        itemStack.setMaterial(type);
        return itemStack;
    }

    /**
     * Changes the NBT of the item to NBT of given string.
     * @param nbtCompound new nbt of the ItemStack
     */
    @Override
    public void writeNBT(@NotNull String nbtCompound) throws NBTException {
        this.nbtCompound = (NBTCompound) new SNBTParser(new StringReader(nbtCompound)).parse();
    }

    /**
     * Resets the NBT of the ItemStack
     */
    @Override
    public void removeNBT() {
        nbtCompound = new NBTCompound();
    }

    /**
     * Adds given amount to the ItemStack.
     * @param amount amount to add
     */
    @Override
    public void add(byte amount) {
        this.amount += amount;
    }

    /**
     * Removes given amount from the ItemStack
     * @param amount amount to remove
     */
    @Override
    public void subtract(byte amount) {
        this.amount -= amount;
    }

    /**
     * @return the copy of the ItemStack with amount of 1
     */
    @Override
    public @NotNull ItemStack single() {
        ItemStack single = clone();
        single.amount = 1;
        return single;
    }

    @Override
    public byte @NotNull [] serialize() {
        return serialize(this);
    }

    public static @NotNull ItemStack deserialize(byte[] bytes) {
        FriendlyByteBuf buf = new FriendlyByteBuf(bytes);
        if(!buf.readBoolean())
            return new ItemStack(Material.AIR);
        ItemStack itemStack = new ItemStack(getMaterial(buf.readVarInt()), buf.readByte());
        NBT nbt = buf.readNBT();
        if(nbt instanceof NBTCompound)
            itemStack.nbtCompound = (NBTCompound) nbt;
        return itemStack;
    }

    public static byte[] serialize(@NotNull ItemStack itemStack) {
        FriendlyByteBuf buf = new FriendlyByteBuf();
        if(itemStack.material == Material.AIR) {
            buf.writeBoolean(false);
            return buf.bytes();
        }
        buf.writeBoolean(true);
        buf.writeVarInt(itemStack.material.getId());
        buf.writeByte(itemStack.amount);
        if(itemStack.nbtCompound.getSize() != 0)
            buf.writeNBT("", itemStack.nbtCompound);
        else
            buf.writeBoolean(false);
        return buf.bytes();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemStack itemStack)) return false;
        return amount == itemStack.amount && material == itemStack.material &&
                Objects.equal(nbtCompound, itemStack.nbtCompound);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(material, amount, nbtCompound);
    }

    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeSlot(this);
    }

}
