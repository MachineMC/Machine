package me.pesekjak.machine.world.biomes;

import lombok.Builder;
import lombok.Getter;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pesekjak.machine.world.biomes.BiomeEffects.DEFAULT_EFFECTS;

@Builder
public class Biome implements NBTSerializable {

    public static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    public static final Biome PLAINS = Biome.builder()
            .category(Category.NONE)
            .name(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "plains"))
            .depth(0.125F)
            .temperature(0.8F)
            .scale(0.05F)
            .downfall(0.4F)
            .effects(DEFAULT_EFFECTS)
            .precipitation(Precipitation.RAIN)
            .temperatureModifier(TemperatureModifier.NONE)
            .build();

    @Getter
    private final int id = ID_COUNTER.getAndIncrement();

    @Getter @NotNull
    private final NamespacedKey name;
    @Getter
    private final float depth;
    @Getter
    private final float temperature;
    @Getter
    private final float scale;
    @Getter
    private final float downfall;
    @Getter @NotNull
    private final Category category;
    @Getter @NotNull
    private final BiomeEffects effects;
    @Getter @NotNull
    private final Precipitation precipitation;
    @Getter @NotNull
    private final TemperatureModifier temperatureModifier;

    @Override
    public NBTCompound toNBT() {
        return NBT.Compound(Map.of(
                "name", NBT.String(name.toString()),
                "id", NBT.Int(id),
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

    public enum Precipitation {
        NONE, RAIN, SNOW;
    }

    public enum Category {
        NONE, TAIGA, EXTREME_HILLS, JUNGLE, MESA, PLAINS,
        SAVANNA, ICY, THE_END, BEACH, FOREST, OCEAN,
        DESERT, RIVER, SWAMP, MUSHROOM, NETHER, UNDERGROUND,
        MOUNTAIN;
    }

    public enum TemperatureModifier {
        NONE, FROZEN;
    }

}
