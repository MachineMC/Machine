package org.machinemc.api.world.dimensions;

import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.server.codec.CodecPart;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
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
    void addDimension(@NotNull DimensionType dimensionType);

    /**
     * Removes the dimension type from the manager if it's registered in this manager.
     * @param dimensionType dimension to remove
     * @return if the dimension was successfully removed
     */
    boolean removeDimension(@NotNull DimensionType dimensionType);

    /**
     * Checks if dimension with given name is registered in
     * the manager.
     * @param name name of the dimension
     * @return if the dimension with given name is registered in this manager
     */
    boolean isRegistered(@NotNull NamespacedKey name);

    /**
     * Checks if the dimension is registered in this manager.
     * @param dimensionType dimension to check
     * @return if the dimension is registered in this manager
     */
    default boolean isRegistered(@NotNull DimensionType dimensionType) {
        return this.equals(dimensionType.getManager()) && isRegistered(dimensionType.getName());
    }

    /**
     * Returns dimension with the given name registered in this manager.
     * @param name name of the dimension
     * @return dimension with given name in this manager
     */
    @Nullable DimensionType getDimension(@NotNull NamespacedKey name);

    /**
     * Returns dimension with given id registered in this manager.
     * @param id id of the dimension
     * @return dimension with given id in this manager
     */
    @Nullable DimensionType getById(@Range(from = 0, to = Integer.MAX_VALUE) int id);

    /**
     * @return unmodifiable set of all dimensions registered in this manager
     */
    @Unmodifiable @NotNull Set<DimensionType> getDimensions();

}
