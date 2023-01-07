package me.pesekjak.machine.world.biomes;

import lombok.*;
import me.pesekjak.machine.utils.NamespacedKey;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.NotNull;

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

    protected final @NotNull AtomicReference<BiomeManager> managerReference = new AtomicReference<>();
    protected final @NotNull AtomicInteger idReference = new AtomicInteger(-1);

    private final @NotNull NamespacedKey name;
    @Builder.Default private final float depth = 0.125F;
    @Builder.Default private final float temperature = 0.8F;
    @Builder.Default private final float scale = 0.05F;
    @Builder.Default private final float downfall = 0.4F;
    @Builder.Default private final @NotNull Category category = Category.NONE;
    @Builder.Default private final @NotNull BiomeEffects effects = BiomeEffectsImpl.createDefault();
    @Builder.Default private final @NotNull Precipitation precipitation = Precipitation.RAIN;
    @Builder.Default private final @NotNull TemperatureModifier temperatureModifier = TemperatureModifier.NONE;

    /**
     * Creates the default biome.
     * @return newly created biome
     */
    public static @NotNull Biome createDefault() {
        return BiomeImpl.builder()
                .name(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "plains"))
                .build();
    }


    @Override
    public @NotNull NBTCompound toNBT() {
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
        return new NBTCompound(Map.of(
                "name", name.toString(),
                "id", idReference.get(),
                "element", element
        ));
    }

}
