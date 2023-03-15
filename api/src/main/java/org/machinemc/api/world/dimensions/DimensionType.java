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
     * @return min y level of the dimension
     */
    @Range(from = -2032, to = 2016) int getMinY();

    /**
     * @return max y level of the dimension
     */
    default @Range(from = 0, to = 4063) int getMaxY() {
        return getHeight() - 1;
    }

    /**
     * @return height of the dimension
     */
    // This method returns the number of block layers, the actual max y level would be one lower
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
