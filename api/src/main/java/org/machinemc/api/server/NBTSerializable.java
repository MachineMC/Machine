package org.machinemc.api.server;

import org.machinemc.api.utils.NBTUtils;
import org.machinemc.nbt.NBTCompound;

import java.io.File;

/**
 * Indicates that the object can be serialized as NBT compound.
 */
public interface NBTSerializable {

    /**
     * @return object serialized as NBT Compound
     */
    NBTCompound toNBT();

    /**
     * Serializes the object into a file in NBT format.
     * @param file file to store the NBT in
     * @return object serialized as NBT Compound
     */
    default NBTCompound serializeNBT(File file) {
        NBTCompound nbtCompound = toNBT();
        NBTUtils.serializeNBT(file, nbtCompound);
        return nbtCompound;
    }

}
