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

import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Dimension type of world.
 */
public interface DimensionType extends NBTSerializable {

    /**
     * @return name of the dimension
     */
    NamespacedKey getName();

    /**
     * @return if the dimension is natural
     */
    boolean isNatural();

    /**
     * @return ambient light of the dimension
     */
    float getAmbientLight();

    /**
     * @return if the ceiling is enabled in the dimension
     */
    boolean isCeilingEnabled();

    /**
     * @return if skylight is enabled in the dimension
     */
    boolean isSkylightEnabled();

    /**
     * @return fixed time of the dimension
     */
    @Nullable Long getFixedTime();

    /**
     * @return if the dimension is capable of raids
     */
    boolean isRaidCapable();

    /**
     * @return if the dimension is respawn anchor safe
     */
    boolean isRespawnAnchorSafe();

    /**
     * @return if the dimension is ultra warm
     */
    boolean isUltrawarm();

    /**
     * @return if the dimension is bed safe
     */
    boolean isBedSafe();

    /**
     * @return effects of the dimension
     */
    NamespacedKey getEffects();

    /**
     * @return if the dimension is piglin safe
     */
    boolean isPiglinSafe();

    /**
     * @return min y level of the dimension where blocks can be placed
     */
    @Range(from = -2032, to = 2016) int getMinY();

    /**
     * @return max y level of the dimension where blocks can be placed
     */
    default int getMaxY() {
        return getMinY() + getHeight() - 1;
    }

    /**
     * @return number of block layers in the dimension
     */
    @Range(from = 1, to = 4064) int getHeight();

    /**
     * @return logical height of the dimension
     */
    int getLogicalHeight();

    /**
     * @return coordinate scale of the dimension
     */
    int getCoordinateScale();

    /**
     * @return infiniburn id of the dimension
     */
    NamespacedKey getInfiniburn();

    /**
     * @return monster spawn block light limit of the dimension
     */
    int getMonsterSpawnBlockLightLimit();

    /**
     * @return monster spawn light level
     */
    int getMonsterSpawnLightLevel();

}
