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
package org.machinemc.api.entities.damagetypes;

import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;

import java.util.Optional;
import java.util.Set;

public interface DamageTypeManager extends CodecPart, ServerProperty {

    /**
     * Registers new damage type to the manager if it's not registered already
     * in a different one.
     * @param damageType damage type to register
     */
    void addDamageType(DamageType damageType);

    /**
     * Removed a damage type with given name if it's registered in this manager.
     * @param name name of the damage type
     * @return if the damage type with given name was successfully removed
     */
    default boolean removeDamageType(NamespacedKey name) {
        return getDamageType(name).map(this::removeDamageType).orElse(false);
    }

    /**
     * Removes the damage type from the manager if it's registered in this manager.
     * @param damageType damage type to remove
     * @return if the damage type was successfully removed
     */
    boolean removeDamageType(DamageType damageType);

    /**
     * Checks if damage type with given name is registered in
     * the manager.
     * @param name name of the damage type
     * @return if the damage type with given name is registered in this manager
     */
    default boolean isRegistered(NamespacedKey name) {
        return getDamageType(name).map(this::isRegistered).orElse(false);
    }

    /**
     * Checks if the damage type is registered in this manager.
     * @param damageType damage type to check
     * @return if the damage type is registered in this manager
     */
    boolean isRegistered(DamageType damageType);

    /**
     * Returns damage type with the given name registered in this manager.
     * @param name name of the damage type
     * @return damage type with given name in this manager
     */
    Optional<DamageType> getDamageType(NamespacedKey name);

    /**
     * Returns damage type with given id registered in this manager.
     * @param id id of the damage type
     * @return damage type with given id in this manager
     */
    Optional<DamageType> getByID(int id);

    /**
     * Returns the id associated with the given damage type registered in this manager.
     * @param damageType the damage type
     * @return the id of the damage type, or -1 if it's not registered
     */
    int getDamageTypeID(DamageType damageType);

    /**
     * @return unmodifiable set of all dimensions registered in this manager
     */
    @Unmodifiable Set<DamageType> getDamageTypes();

    /**
     * Returns the NBT compound of a damage type with the given name.
     * @param name name of the damage type
     * @return NBT of the given damage type
     */
    default Optional<NBTCompound> getDamageTypeNBT(NamespacedKey name) {
        return getDamageType(name).map(this::getDamageTypeNBT);
    }

    /**
     * Returns the NBT compound of the given damage type.
     * @param damageType the damage type
     * @return NBT of the given damage type
     */
    NBTCompound getDamageTypeNBT(DamageType damageType);

}
