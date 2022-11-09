package me.pesekjak.machine.world.biomes;

import lombok.*;
import me.pesekjak.machine.server.NBTSerializable;
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
public class Biome implements NBTSerializable {

    @Getter(AccessLevel.PROTECTED)
    protected final AtomicReference<BiomeManager> manager = new AtomicReference<>();
    protected final AtomicInteger id = new AtomicInteger(-1);

    @NotNull
    private final NamespacedKey name;
    private final float depth;
    private final float temperature;
    private final float scale;
    private final float downfall;
    @NotNull
    private final Category category;
    @NotNull
    private final BiomeEffects effects;
    @NotNull
    private final Precipitation precipitation;
    @NotNull
    private final TemperatureModifier temperatureModifier;

    /**
     * Creates the default biome.
     * @return newly created biome
     */
    public static Biome createDefault() {
        return Biome.builder()
                .name(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "plains"))
                .depth(0.125F)
                .temperature(0.8F)
                .scale(0.05F)
                .downfall(0.4F)
                .category(Category.NONE)
                .effects(BiomeEffects.createDefault())
                .precipitation(Precipitation.RAIN)
                .temperatureModifier(TemperatureModifier.NONE)
                .build();
    }

    /**
     * @return manager of the world
     */
    public BiomeManager manager() {
        return manager.get();
    }

    public int getId() {
        if(manager.get() == null) return -1;
        return id.get();
    }

    @Override
    public NBTCompound toNBT() {
        return NBT.Compound(Map.of(
                "name", NBT.String(name.toString()),
                "id", NBT.Int(id.get()),
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

    /**
     * Raining type in the biome.
     */
    public enum Precipitation {
        NONE, RAIN, SNOW;
    }

    /**
     * Category of the biome.
     */
    public enum Category {
        NONE, TAIGA, EXTREME_HILLS, JUNGLE, MESA, PLAINS,
        SAVANNA, ICY, THE_END, BEACH, FOREST, OCEAN,
        DESERT, RIVER, SWAMP, MUSHROOM, NETHER, UNDERGROUND,
        MOUNTAIN;
    }

    /**
     * Temperature of the biome.
     */
    public enum TemperatureModifier {
        NONE, FROZEN;
    }

}
