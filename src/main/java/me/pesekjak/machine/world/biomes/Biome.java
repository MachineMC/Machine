package me.pesekjak.machine.world.biomes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Locale;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Biome implements NBTSerializable {

    @Getter
    private final int id;
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

    public static Biome createDefault(BiomeManager manager) {
        return Biome.builder(manager)
                .category(Category.NONE)
                .name(new NamespacedKey(NamespacedKey.MINECRAFT_NAMESPACE, "plains"))
                .depth(0.125F)
                .temperature(0.8F)
                .scale(0.05F)
                .downfall(0.4F)
                .effects(BiomeEffects.createDefault())
                .precipitation(Precipitation.RAIN)
                .temperatureModifier(TemperatureModifier.NONE)
                .build();
    }

    public static BiomeBuilder builder(BiomeManager manager) {
        return new BiomeBuilder(manager);
    }

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


    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static final class BiomeBuilder {
        private final BiomeManager manager;
        private NamespacedKey name;
        private float depth;
        private float temperature;
        private float scale;
        private float downfall;
        private Category category;
        private BiomeEffects effects;
        private Precipitation precipitation;
        private TemperatureModifier temperatureModifier;
        public BiomeBuilder name(NamespacedKey name) {
            this.name = name;
            return this;
        }
        public BiomeBuilder depth(float depth) {
            this.depth = depth;
            return this;
        }
        public BiomeBuilder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }
        public BiomeBuilder scale(float scale) {
            this.scale = scale;
            return this;
        }
        public BiomeBuilder downfall(float downfall) {
            this.downfall = downfall;
            return this;
        }
        public BiomeBuilder category(Category category) {
            this.category = category;
            return this;
        }
        public BiomeBuilder effects(BiomeEffects effects) {
            this.effects = effects;
            return this;
        }
        public BiomeBuilder precipitation(Precipitation precipitation) {
            this.precipitation = precipitation;
            return this;
        }
        public BiomeBuilder temperatureModifier(TemperatureModifier temperatureModifier) {
            this.temperatureModifier = temperatureModifier;
            return this;
        }
        public Biome build() {
            return new Biome(
                    manager.ID_COUNTER.getAndIncrement(),
                    name,
                    depth,
                    temperature,
                    scale,
                    downfall,
                    category,
                    effects,
                    precipitation,
                    temperatureModifier
            );
        }
    }

}
