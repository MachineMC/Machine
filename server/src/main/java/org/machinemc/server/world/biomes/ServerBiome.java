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
import java.util.Objects;

/**
 * Default biome implementation.
 */
@Getter
@Builder
public class ServerBiome implements Biome {

    private final NamespacedKey name;
    @Builder.Default private float depth = 0.125F;
    @Builder.Default private float temperature = 0.8F;
    @Builder.Default private float scale = 0.05F;
    @Builder.Default private float downfall = 0.4F;
    @Builder.Default private Category category = Category.NONE;
    @Builder.Default private BiomeEffects effects = ServerBiomeEffects.createDefault();
    @Builder.Default private Precipitation precipitation = Precipitation.RAIN;
    @Builder.Default private TemperatureModifier temperatureModifier = TemperatureModifier.NONE;

    ServerBiome(final NamespacedKey name,
                final float depth,
                final float temperature,
                final float scale,
                final float downfall,
                final Category category,
                final BiomeEffects effects,
                final Precipitation precipitation,
                final TemperatureModifier temperatureModifier) {
        this.name = Objects.requireNonNull(name, "Name of biome can not be null");
        this.depth = depth;
        this.temperature = temperature;
        this.scale = scale;
        this.downfall = downfall;
        this.category = category;
        this.effects = effects;
        this.precipitation = precipitation;
        this.temperatureModifier = temperatureModifier;
    }

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
