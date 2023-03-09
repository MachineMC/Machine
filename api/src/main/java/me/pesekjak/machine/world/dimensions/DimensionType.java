package me.pesekjak.machine.world.dimensions;

import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Dimension type of world.
 */
public interface DimensionType extends NBTSerializable {

    /**
     * @return name of the dimension
     */
    @NotNull NamespacedKey getName();

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
    @NotNull NamespacedKey getEffects();

    /**
     * @return if the dimension is piglin safe
     */
    boolean isPiglinSafe();

    /**
     * @return min y level of the dimension
     */
    @Range(from = -2032, to = 2016) int getMinY();

    /**
     * @return height of the dimension
     */
    @Range(from = 0, to = 4064) int getHeight();

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
    @NotNull NamespacedKey getInfiniburn();

    /**
     * @return monster spawn block light limit of the dimension
     */
    int getMonsterSpawnBlockLightLimit();

    /**
     * @return monster spawn light level
     */
    int getMonsterSpawnLightLevel();

}
