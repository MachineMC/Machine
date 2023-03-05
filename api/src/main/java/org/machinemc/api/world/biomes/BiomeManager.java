package org.machinemc.api.world.biomes;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface BiomeManager extends CodecPart, ServerProperty {

    /**
     * Registers new biome to the manager if it's not registered already
     * in a different one.
     * @param biome biome to register
     */
    void addBiome(Biome biome);

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
    boolean isRegistered(NamespacedKey name);

    /**
     * Checks if the biome is registered in this manager.
     * @param biome biome to check
     * @return if the biome is registered in this manager
     */
    default boolean isRegistered(Biome biome) {
        return this.equals(biome.getManager());
    }

    /**
     * Returns biome with the given name registered in this manager.
     * @param name name of the biome
     * @return biome with given name in this manager
     */
    @Nullable Biome getBiome(NamespacedKey name);

    /**
     * Returns biome with given id registered in this manager.
     * @param id id of the biome
     * @return biome with given id in this manager
     */
    @Nullable Biome getById(int id);

    /**
     * @return unmodifiable set of all biomes registered in this manager
     */
    @Unmodifiable Set<Biome> getBiomes();

}
