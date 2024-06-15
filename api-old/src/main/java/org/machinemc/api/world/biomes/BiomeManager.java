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
package org.machinemc.api.world.biomes;

import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;

import java.util.Optional;
import java.util.Set;

public interface BiomeManager extends CodecPart, ServerProperty {

    /**
     * Registers new biome to the manager if it's not registered already
     * in a different one.
     * @param biome biome to register
     */
    void addBiome(Biome biome);

    /**
     * Removed a biome with given name if it's registered in this manager.
     * @param name name of the biome
     * @return if the biome with given name was successfully removed
     */
    default boolean removeBiome(NamespacedKey name) {
        return getBiome(name).map(this::removeBiome).orElse(false);
    }

    /**
     * Removes a biome from the manager if it's registered in this manager.
     * @param biome biome to unregister
     * @return if the biome was successfully removed
     */
    boolean removeBiome(Biome biome);

    /**
     * Checks if biome with given name is registered in
     * the manager.
     * @param name name of the biome
     * @return if the biome with given name is registered in this manager
     */
    default boolean isRegistered(NamespacedKey name) {
        return getBiome(name).map(this::isRegistered).orElse(false);
    }

    /**
     * Checks if the biome is registered in this manager.
     * @param biome biome to check
     * @return if the biome is registered in this manager
     */
    boolean isRegistered(Biome biome);

    /**
     * Returns biome with the given name registered in this manager.
     * @param name name of the biome
     * @return biome with given name in this manager
     */
    Optional<Biome> getBiome(NamespacedKey name);

    /**
     * Returns biome with given id registered in this manager.
     * @param id id of the biome
     * @return biome with given id in this manager
     */
    Optional<Biome> getByID(int id);

    /**
     * Returns the id associated with the given biome registered in this manager.
     * @param biome the biome
     * @return the id of the dimension, or -1 if it's not registered
     */
    int getBiomeID(Biome biome);

    /**
     * @return unmodifiable set of all biomes registered in this manager
     */
    @Unmodifiable Set<Biome> getBiomes();

    /**
     * Returns the NBT compound of a dimension with the given name.
     * @param name name of the dimension
     * @return NBT of the given dimension
     */
    default Optional<NBTCompound> getBiomeNBT(NamespacedKey name) {
        return getBiome(name).map(this::getBiomeNBT);
    }

    /**
     * Returns the NBT compound of the given biome.
     * @param biome the biome
     * @return NBT of the given biome
     */
    NBTCompound getBiomeNBT(Biome biome);

}
