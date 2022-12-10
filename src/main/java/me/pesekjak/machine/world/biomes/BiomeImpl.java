package me.pesekjak.machine.world.biomes;

import lombok.*;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

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
        return NBT.Compound(Map.of(
                "name", NBT.String(name.toString()),
                "id", NBT.Int(idReference.get()),
                "element", NBT.Compound(element -> {
                    element.setFloat("depth", depth);
                    element.setFloat("temperature", temperature);
                    element.setFloat("scale", scale);
                    element.setFloat("downfall", downfall);
                    element.setString("category", category.name().toLowerCase(Locale.ROOT));
                    element.setString("precipitation", precipitation.name().toLowerCase(Locale.ROOT));
                    if (temperatureModifier != TemperatureModifier.NONE)
                        element.setString("temperature_modifier", temperatureModifier.name().toLowerCase(Locale.ROOT));
                    element.set("effects", effects.toNBT());
                })
        ));
    }

}
