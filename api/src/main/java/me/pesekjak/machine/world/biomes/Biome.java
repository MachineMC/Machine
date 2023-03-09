package me.pesekjak.machine.world.biomes;

import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface Biome extends NBTSerializable {

    /**
     * @return name of the biome
     */
    @NotNull NamespacedKey getName();

    /**
     * @return depth of the biome
     */
    float getDepth();

    /**
     * @return temperature of the biome
     */
    float getTemperature();

    /**
     * @return scale of the biome
     */
    float getScale();

    /**
     * @return downfall of the biome
     */
    float getDownfall();

    /**
     * @return category of the biome
     */
    @NotNull Category getCategory();

    /**
     * @return biome effects of the biome
     */
    @NotNull BiomeEffects getEffects();

    /**
     * @return precipitation of the biome
     */
    @NotNull Precipitation getPrecipitation();

    /**
     * @return temperature modifier of the biome
     */
    @NotNull TemperatureModifier getTemperatureModifier();

    /**
     * Represents the raining type in the biome.
     */
    enum Precipitation {
        NONE, RAIN, SNOW;
    }

    /**
     * Represents category of the biome.
     */
    enum Category {
        NONE, TAIGA, EXTREME_HILLS, JUNGLE, MESA, PLAINS,
        SAVANNA, ICY, THE_END, BEACH, FOREST, OCEAN,
        DESERT, RIVER, SWAMP, MUSHROOM, NETHER, UNDERGROUND,
        MOUNTAIN;
    }

    /**
     * Represents the temperature of the biome.
     */
    enum TemperatureModifier {
        NONE, FROZEN;
    }

}
