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
package org.machinemc.api.world.dimensions;

import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;

import java.util.Optional;
import java.util.Set;

/**
 * Manager for dimension types.
 */
public interface DimensionTypeManager extends CodecPart, ServerProperty {

    /**
     * Registers new dimension to the manager if it's not registered already
     * in a different one.
     * @param dimensionType dimension to register
     */
    void addDimension(DimensionType dimensionType);

    /**
     * Removed a dimension with given name if it's registered in this manager.
     * @param name name of the dimension
     * @return if the dimension with given name was successfully removed
     */
    default boolean removeDimension(NamespacedKey name) {
        return getDimension(name).map(this::removeDimension).orElse(false);
    }

    /**
     * Removes the dimension type from the manager if it's registered in this manager.
     * @param dimensionType dimension to remove
     * @return if the dimension was successfully removed
     */
    boolean removeDimension(DimensionType dimensionType);

    /**
     * Checks if dimension with given name is registered in
     * the manager.
     * @param name name of the dimension
     * @return if the dimension with given name is registered in this manager
     */
    default boolean isRegistered(NamespacedKey name) {
        return getDimension(name).map(this::isRegistered).orElse(false);
    }

    /**
     * Checks if the dimension is registered in this manager.
     * @param dimensionType dimension to check
     * @return if the dimension is registered in this manager
     */
    boolean isRegistered(DimensionType dimensionType);

    /**
     * Returns dimension with the given name registered in this manager.
     * @param name name of the dimension
     * @return dimension with given name in this manager
     */
    Optional<DimensionType> getDimension(NamespacedKey name);

    /**
     * Returns dimension with given id registered in this manager.
     * @param id id of the dimension
     * @return dimension with given id in this manager
     */
    Optional<DimensionType> getByID(int id);

    /**
     * Returns the id associated with the given dimension registered in this manager.
     * @param dimensionType the dimension
     * @return the id of the dimension, or -1 if it's not registered
     */
    int getDimensionID(DimensionType dimensionType);

    /**
     * @return unmodifiable set of all dimensions registered in this manager
     */
    @Unmodifiable Set<DimensionType> getDimensions();

    /**
     * Returns the NBT compound of a dimension with the given name.
     * @param name name of the dimension
     * @return NBT of the given dimension
     */
    default Optional<NBTCompound> getDimensionNBT(NamespacedKey name) {
        return getDimension(name).map(this::getDimensionNBT);
    }

    /**
     * Returns the NBT compound of the given dimension.
     * @param dimensionType the dimension
     * @return NBT of the given dimension
     */
    NBTCompound getDimensionNBT(DimensionType dimensionType);

}
