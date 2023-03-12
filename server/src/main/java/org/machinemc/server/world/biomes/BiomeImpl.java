package org.machinemc.server.world.biomes;

import lombok.*;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.utils.LazyNamespacedKey;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeEffects;
import org.machinemc.api.world.biomes.BiomeManager;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Default biome implementation.
 */
@Builder
@Getter
public class BiomeImpl implements Biome {

    private final @NotNull NamespacedKey name;
    @Builder.Default private final float depth = 0.125F;
    @Builder.Default private final float temperature = 0.8F;
    @Builder.Default private final float scale = 0.05F;
    @Builder.Default private final float downfall = 0.4F;
    @Builder.Default private final Category category = Category.NONE;
    @Builder.Default private final BiomeEffects effects = BiomeEffectsImpl.createDefault();
    @Builder.Default private final Precipitation precipitation = Precipitation.RAIN;
    @Builder.Default private final TemperatureModifier temperatureModifier = TemperatureModifier.NONE;

    /**
     * Creates the default biome.
     * @return newly created biome
     */
    public static Biome createDefault() {
        return BiomeImpl.builder()
                .name(LazyNamespacedKey.of(NamespacedKey.MINECRAFT_NAMESPACE, "plains"))
                .build();
    }


    @Override
    public NBTCompound toNBT() {
        NBTCompound element = new NBTCompound(Map.of(
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

}
