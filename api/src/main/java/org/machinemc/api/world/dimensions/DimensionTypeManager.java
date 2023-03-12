package org.machinemc.api.world.dimensions;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.api.utils.NamespacedKey;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.server.codec.CodecPart;
import me.pesekjak.machine.utils.NamespacedKey;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

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
        DimensionType dimensionType = getDimension(name);
        if (dimensionType == null)
            return false;
        return isRegistered(dimensionType);
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
    @Nullable DimensionType getDimension(NamespacedKey name);

    /**
     * Returns dimension with given id registered in this manager.
     * @param id id of the dimension
     * @return dimension with given id in this manager
     */
    @Nullable DimensionType getById(int id);

    /**
     * Returns the id associated with the given dimension registered in this manager.
     * @param dimensionType the dimension
     * @return the id of the dimension, or -1 if it's not registered
     */
    int getDimensionId(DimensionType dimensionType);

    /**
     * @return unmodifiable set of all dimensions registered in this manager
     */
    @Unmodifiable Set<DimensionType> getDimensions();

    /**
     * Returns the NBT compound of a dimension with the given name
     * @param name name of the dimension
     * @return NBT of the given dimension
     */
    default @Nullable NBTCompound getDimensionNBT(NamespacedKey name) {
        DimensionType dimensionType = getDimension(name);
        if (dimensionType == null)
            return null;
        return getDimensionNBT(dimensionType);
    }

    /**
     * Returns the NBT compound of the given dimension
     * @param dimensionType the dimension
     * @return NBT of the given dimension
     */
    NBTCompound getDimensionNBT(DimensionType dimensionType);

}
