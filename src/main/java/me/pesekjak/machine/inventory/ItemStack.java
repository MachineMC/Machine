package me.pesekjak.machine.inventory;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.Material;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;

import java.io.StringReader;

/**
 * Represents an item in inventory.
 */
public class ItemStack implements Cloneable {

    private final static Material[] REGISTRY;

    @Getter @Setter
    private Material material;
    @Getter @Setter
    private int amount = 1;
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
    public static Material getMaterial(int id) {
        if(id == -1) return null;
        if(REGISTRY.length <= id) return null;
        return REGISTRY[id];
    }

    /**
     * Returns id of the material
     * @param material material to get id from
     * @return id of the material
     */
    public static int getId(Material material) {
        return material.getId();
    }

    public ItemStack(Material material) {
        if(material.getId() == -1)
            throw new IllegalStateException("Material " + material + " can't have item form");
        this.material = material;
    }

    public ItemStack(Material material, int amount) {
        this(material);
        this.amount = amount;
    }

    public Material getType() {
        return material;
    }

    public void setType(Material material) {
        this.material = material;
    }

    /**
     * Changes the NBT of the item to NBT of given string.
     * @param nbtCompound new nbt of the ItemStack
     */
    public void writeNBT(String nbtCompound) throws NBTException {
        this.nbtCompound = (NBTCompound) new SNBTParser(new StringReader(nbtCompound)).parse();
    }

    /**
     * Resets the NBT of the ItemStack
     */
    public void removeNBT() {
        nbtCompound = new NBTCompound();
    }

    /**
     * Adds given amount to the ItemStack.
     * @param amount amount to add
     */
    public void add(int amount) {
        this.amount += amount;
    }

    /**
     * Adds 1 to the amount of the ItemStack.
     */
    public void add() {
        add(1);
    }

    /**
     * Removes given amount from the ItemStack
     * @param amount amount to remove
     */
    public void subtract(int amount) {
        this.amount -= amount;
    }

    /**
     * Removes 1 from the amount of the ItemStack
     */
    public void subtract() {
        subtract(1);
    }

    /**
     * @return the copy of the ItemStack with amount of 1
     */
    public ItemStack single() {
        ItemStack single = clone();
        single.amount = 1;
        return single;
    }

    public byte[] serialize() {
        return serialize(this);
    }

    public static ItemStack deserialize(byte[] bytes) {
        FriendlyByteBuf buf = new FriendlyByteBuf(bytes);
        if(!buf.readBoolean())
            return new ItemStack(Material.AIR);
        ItemStack itemStack = new ItemStack(getMaterial(buf.readVarInt()), buf.readByte());
        NBT nbt = buf.readNBT();
        if(nbt instanceof NBTCompound)
            itemStack.nbtCompound = (NBTCompound) nbt;
        return itemStack;
    }

    public static byte[] serialize(ItemStack itemStack) {
        FriendlyByteBuf buf = new FriendlyByteBuf();
        if(itemStack.material == Material.AIR || itemStack.amount < 1 || itemStack.amount > Byte.MAX_VALUE) {
            buf.writeBoolean(false);
            return buf.bytes();
        }
        buf.writeBoolean(true);
        buf.writeVarInt(itemStack.material.getId());
        buf.writeByte((byte) itemStack.amount);
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

}
