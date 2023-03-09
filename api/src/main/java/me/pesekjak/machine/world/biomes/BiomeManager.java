package me.pesekjak.machine.world.biomes;

import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.server.codec.CodecPart;
import me.pesekjak.machine.utils.NamespacedKey;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface BiomeManager extends CodecPart, ServerProperty {

    /**
     * Registers new biome to the manager if it's not registered already
     * in a different one.
     * @param biome biome to register
     */
    void addBiome(@NotNull Biome biome);

    /**
     * Removes a biome from the manager if it's registered in this manager.
     * @param biome biome to unregister
     * @return if the biome was successfully removed
     */
    boolean removeBiome(@NotNull Biome biome);

    /**
     * Checks if biome with given name is registered in
     * the manager.
     * @param name name of the biome
     * @return if the biome with given name is registered in this manager
     */
    default boolean isRegistered(@NotNull NamespacedKey name) {
        Biome biome = getBiome(name);
        if (biome == null)
            return false;
        return isRegistered(biome);
    }

    /**
     * Checks if the biome is registered in this manager.
     * @param biome biome to check
     * @return if the biome is registered in this manager
     */
    boolean isRegistered(@NotNull Biome biome);

    /**
     * Returns biome with the given name registered in this manager.
     * @param name name of the biome
     * @return biome with given name in this manager
     */
    @Nullable Biome getBiome(@NotNull NamespacedKey name);

    /**
     * Returns biome with given id registered in this manager.
     * @param id id of the biome
     * @return biome with given id in this manager
     */
    @Nullable Biome getById(@Range(from = 0, to = Integer.MAX_VALUE) int id);

    /**
     * Returns the id associated with the given biome registered in this manager.
     * @param biome the biome
     * @return the id of the dimension, or -1 if it's not registered
     */
    int getBiomeId(Biome biome);

    /**
     * @return unmodifiable set of all biomes registered in this manager
     */
    @Unmodifiable @NotNull Set<Biome> getBiomes();

    /**
     * Returns the NBT compound of a dimension with the given name
     * @param name name of the dimension
     * @return NBT of the given dimension
     */
    default @Nullable NBTCompound getBiomeNBT(NamespacedKey name) {
        Biome biome = getBiome(name);
        if (biome == null)
            return null;
        return getBiomeNBT(biome);
    }

    /**
     * Returns the NBT compound of the given biome
     * @param biome the biome
     * @return NBT of the given biome
     */
    NBTCompound getBiomeNBT(Biome biome);

}
