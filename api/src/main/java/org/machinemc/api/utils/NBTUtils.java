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
package org.machinemc.api.utils;

import org.jetbrains.annotations.Contract;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for NBT-related operations.
 */
public final class NBTUtils {

    private NBTUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates an NBT list from objects.
     * Note: the objects must all be of the same type
     * @param objects The objects to use
     * @return NBT double list
     */
    @Contract("_ -> new")
    public static NBTList list(final Object... objects) {
        return new NBTList(objects);
    }

    /**
     * Serializes NBT to a file (creates the file if it doesn't exist).
     * @param file The file to serialize to
     * @param nbt The NBT to serialize
     */
    public static void serializeNBT(final File file, final NBTCompound nbt) {
        try {
            if (!file.exists() && !file.createNewFile())
                throw new IOException("Unable to create file at " + file.getAbsolutePath());
            nbt.write(file);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Returns NBT from a file.
     * @param file The file to deserialize from
     * @return The NBT stored in the file
     */
    public static NBTCompound deserializeNBTFile(final File file) {
        try {
            final NBTCompound compound = new NBTCompound();
            compound.read(file);
            return compound;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read file at " + file.getAbsolutePath(), e);
        }
    }

}
