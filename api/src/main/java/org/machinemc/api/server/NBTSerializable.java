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
        final NBTCompound nbtCompound = toNBT();
        NBTUtils.serializeNBT(file, nbtCompound);
        return nbtCompound;
    }

}
