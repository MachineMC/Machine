package me.pesekjak.machine.server;

import me.pesekjak.machine.utils.NBTUtils;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Indicates that the object can be serialized as NBT compound.
 */
public interface NBTSerializable {

    /**
     * @return object serialized as NBT Compound
     */
    @NotNull NBTCompound toNBT();

    /**
     * Serializes the object into a file in NBT format.
     * @param file file to store the NBT in
     * @return object serialized as NBT Compound
     */
    default @NotNull NBTCompound serializeNBT(@NotNull File file) {
        NBTCompound nbtCompound = toNBT();
        NBTUtils.serializeNBT(file, nbtCompound);
        return nbtCompound;
    }

}
