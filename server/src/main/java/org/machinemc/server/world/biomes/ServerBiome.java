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
package org.machinemc.server.world.biomes;

import lombok.*;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeEffects;

import java.util.Locale;
import java.util.Map;

/**
 * Default biome implementation.
 */
@Builder
@Getter
public class ServerBiome implements Biome {

    private final NamespacedKey name;
    @Builder.Default private final float depth = 0.125F;
    @Builder.Default private final float temperature = 0.8F;
    @Builder.Default private final float scale = 0.05F;
    @Builder.Default private final float downfall = 0.4F;
    @Builder.Default private final Category category = Category.NONE;
    @Builder.Default private final BiomeEffects effects = ServerBiomeEffects.createDefault();
    @Builder.Default private final Precipitation precipitation = Precipitation.RAIN;
    @Builder.Default private final TemperatureModifier temperatureModifier = TemperatureModifier.NONE;

    /**
     * Creates the default biome.
     * @return newly created biome
     */
    public static Biome createDefault() {
        return ServerBiome.builder()
                .name(NamespacedKey.of(NamespacedKey.MINECRAFT_NAMESPACE, "plains"))
                .build();
    }


    @Override
    public NBTCompound toNBT() {
        final NBTCompound element = new NBTCompound(Map.of(
                "depth", depth,
                "temperature", temperature,
                "scale", scale,
                "downfall", downfall,
                "category", category.name().toLowerCase(Locale.ROOT),
                "precipitation", precipitation.name().toLowerCase(Locale.ROOT),
                "effects", effects.toNBT()
        ));
        if (temperatureModifier != TemperatureModifier.NONE)
            element.set("temperature_modifier", temperatureModifier.name().toLowerCase(Locale.ROOT));
        return element;
    }

    @Override
    public String toString() {
        return "ServerBiome("
                + "name=" + name
                + ')';
    }
}
