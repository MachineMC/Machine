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

import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;

public interface Biome extends NBTSerializable {

    /**
     * @return name of the biome
     */
    NamespacedKey getName();

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
     * @return whether the biome has precipitation
     */
    boolean hasPrecipitation();

    /**
     * @return category of the biome
     */
    Category getCategory();

    /**
     * @return biome effects of the biome
     */
    BiomeEffects getEffects();

    /**
     * @return temperature modifier of the biome
     */
    TemperatureModifier getTemperatureModifier();

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
