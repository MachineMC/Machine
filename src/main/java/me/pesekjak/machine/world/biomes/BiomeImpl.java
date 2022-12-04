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
 * Represents a biome of a world.
 */
@Builder
@Getter
public class BiomeImpl implements Biome {

    protected final AtomicReference<BiomeManager> managerReference = new AtomicReference<>();
    protected final AtomicInteger idReference = new AtomicInteger(-1);

    @NotNull
    private final NamespacedKey name;
    private final float depth;
    private final float temperature;
    private final float scale;
    private final float downfall;
    @NotNull
    private final Category category;
    @NotNull
    private final BiomeEffectsImpl effects;
    @NotNull
    private final Precipitation precipitation;
    @NotNull
    private final TemperatureModifier temperatureModifier;

    /**
     * Creates the default biome.
     * @return newly created biome
     */
    public static BiomeImpl createDefault() {
        return BiomeImpl.builder()
                .name(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "plains"))
                .depth(0.125F)
                .temperature(0.8F)
                .scale(0.05F)
                .downfall(0.4F)
                .category(Category.NONE)
                .effects(BiomeEffectsImpl.createDefault())
                .precipitation(Precipitation.RAIN)
                .temperatureModifier(TemperatureModifier.NONE)
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
