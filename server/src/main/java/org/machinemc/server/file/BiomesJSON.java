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
package org.machinemc.server.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.machinemc.api.Server;
import org.machinemc.api.file.ServerFile;
import org.machinemc.api.particles.Particle;
import org.machinemc.api.server.ServerProperty;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeEffects;
import org.machinemc.nbt.parser.NBTParser;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;
import org.machinemc.server.Machine;
import org.machinemc.server.world.biomes.ServerBiome;
import org.machinemc.server.world.biomes.ServerBiomeEffects;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Represents biomes json server file.
 */
public class BiomesJSON implements ServerFile, ServerProperty {

    public static final String BIOMES_FILE_NAME = "biomes.json";

    @Getter
    private final Server server;
    private final Set<Biome> biomes = new LinkedHashSet<>();

    public BiomesJSON(final Server server, final File file) throws IOException {
        this.server = Objects.requireNonNull(server, "Server can not be null");
        Objects.requireNonNull(file, "Source file can not be null");
        final JsonParser parser = new JsonParser();
        final JsonObject biomes;
        try (FileReader fileReader = new FileReader(file)) {
            biomes = parser.parse(fileReader).getAsJsonObject();
        }

        for (final Map.Entry<String, JsonElement> biomeKey : biomes.entrySet()) {
            final Biome original = ServerBiome.createDefault();
            final NamespacedKey key;
            try {
                key = NamespacedKey.parse(biomeKey.getKey());
            } catch (Exception ignored) {
                server.getConsole().severe("Biome '" + biomeKey.getKey()
                        + "' uses illegal identifier and can't be registered");
                continue;
            }

            final JsonObject biome = biomeKey.getValue().getAsJsonObject();

            Biome.Category category;
            try {
                category = Biome.Category.valueOf(biome.get("category").getAsString().toUpperCase());
            } catch (Exception ignored) {
                category = original.getCategory();
            }

            Biome.TemperatureModifier temperatureModifier;
            try {
                temperatureModifier = Biome.TemperatureModifier.valueOf(
                        biome.get("temperature_modifier").getAsString().toUpperCase()
                );
            } catch (Exception ignored) {
                temperatureModifier = original.getTemperatureModifier();
            }

            BiomeEffects effects;
            try {
                final JsonObject effectsJson = biome.getAsJsonObject("effects");
                effects = original.getEffects();
                final int fogColor = effectsJson.has("fog_color")
                        ? effectsJson.get("fog_color").getAsInt()
                        : effects.getFogColor().getRGB();
                final int skyColor = effectsJson.has("sky_color")
                        ? effectsJson.get("sky_color").getAsInt()
                        : effects.getSkyColor().getRGB();
                final int waterColor = effectsJson.has("water_color")
                        ? effectsJson.get("water_color").getAsInt()
                        : effects.getWaterColor().getRGB();
                final int waterFogColor = effectsJson.has("water_fog_color")
                        ? effectsJson.get("water_fog_color").getAsInt()
                        : effects.getWaterFogColor().getRGB();
                final Integer foliageColor;
                if (effectsJson.has("foliage_color"))
                    foliageColor = effectsJson.get("foliage_color").getAsInt();
                else
                    foliageColor = effects.getFoliageColor().map(Colour::getRGB).orElse(null);
                final Integer grassColor;
                if (effectsJson.has("grass_color"))
                    grassColor = effectsJson.get("grass_color").getAsInt();
                else
                    grassColor = effects.getGrassColor().map(Colour::getRGB).orElse(null);

                BiomeEffects.GrassColorModifier grassModifier;
                try {
                    grassModifier = BiomeEffects.GrassColorModifier.valueOf(
                            effectsJson.get("grass_color_modifier").getAsString().toUpperCase()
                    );
                } catch (Exception ignored) {
                    grassModifier = effects.getGrassColorModifier().orElse(null);
                }
                NamespacedKey ambientSound;
                try {
                    ambientSound = NamespacedKey.parse(effectsJson.get("ambient_sound").getAsString());
                } catch (Exception ignored) {
                    ambientSound = effects.getAmbientSound().orElse(null);
                }

                BiomeEffects.MoodSound moodSound;
                try {
                    final JsonObject moodSoundJson = effectsJson.getAsJsonObject("mood_sound");
                    final NamespacedKey sound = NamespacedKey.parse(moodSoundJson.get("sound").getAsString());
                    final int tickDelay = moodSoundJson.get("tick_delay").getAsInt();
                    final int blockSearchExtent = moodSoundJson.get("block_search_extent").getAsInt();
                    final double offset = moodSoundJson.get("offset").getAsDouble();
                    moodSound = new ServerBiomeEffects.MoodSoundImpl(sound, tickDelay, blockSearchExtent, offset);
                } catch (Exception ignored) {
                    moodSound = effects.getMoodSound().orElse(null);
                }

                BiomeEffects.AdditionsSound additionsSound;
                try {
                    final JsonObject additionsSoundJson = effectsJson.getAsJsonObject("additions_sound");
                    final NamespacedKey sound = NamespacedKey.parse(additionsSoundJson.get("sound").getAsString());
                    final double tickChance = additionsSoundJson.get("tick_chance").getAsDouble();
                    additionsSound = new ServerBiomeEffects.AdditionsSoundImpl(sound, tickChance);
                } catch (Exception ignored) {
                    additionsSound = effects.getAdditionsSound().orElse(null);
                }

                BiomeEffects.Music music;
                try {
                    final JsonObject musicJson = effectsJson.getAsJsonObject("music");
                    final NamespacedKey sound = NamespacedKey.parse(musicJson.get("sound").getAsString());
                    final int minDelay = musicJson.get("min_delay").getAsInt();
                    final int maxDelay = musicJson.get("max_delay").getAsInt();
                    final boolean replaceCurrentMusic = musicJson.get("replace_current_music").getAsBoolean();
                    music = new ServerBiomeEffects.MusicImpl(sound, minDelay, maxDelay, replaceCurrentMusic);
                } catch (Exception ignored) {
                    music = effects.getMusic().orElse(null);
                }

                Float biomeParticleProbability;
                Particle<?> particle;
                try {
                    final JsonObject particleJson = effectsJson.getAsJsonObject("particle");
                    biomeParticleProbability = particleJson.get("probability").getAsFloat();
                    particle = Particle.fromNBT(new NBTParser(particleJson.get("options").getAsString()).parse())
                            .orElse(null);
                } catch (Exception ignored) {
                    biomeParticleProbability = effects.getBiomeParticleProbability().orElse(null);
                    particle = effects.getBiomeParticle().orElse(null);
                }

                effects = ServerBiomeEffects.builder()
                        .fogColor(new HexColor(fogColor))
                        .skyColor(new HexColor(skyColor))
                        .waterColor(new HexColor(waterColor))
                        .waterFogColor(new HexColor(waterFogColor))
                        .foliageColor(foliageColor != null ? new HexColor(foliageColor) : null)
                        .grassColor(grassColor != null ? new HexColor(grassColor) : null)
                        .grassColorModifier(grassModifier)
                        .ambientSound(ambientSound)
                        .moodSound(moodSound)
                        .additionsSound(additionsSound)
                        .music(music)
                        .biomeParticleProbability(biomeParticleProbability)
                        .biomeParticle(particle)
                        .build();

            } catch (Exception ignored) {
                effects = original.getEffects();
            }

            this.biomes.add(ServerBiome.builder()
                    .name(key)
                    .depth(biome.has("depth")
                            ? biome.get("depth").getAsFloat()
                            : original.getDepth())
                    .temperature(biome.has("temperature")
                            ? biome.get("temperature").getAsFloat()
                            : original.getTemperature())
                    .scale(biome.has("scale")
                            ? biome.get("scale").getAsFloat()
                            : original.getScale())
                    .downfall(biome.has("downfall")
                            ? biome.get("downfall").getAsFloat()
                            : original.getDownfall())
                    .precipitation(biome.has("has_precipitation")
                            ? biome.get("has_precipitation").getAsBoolean()
                            : original.hasPrecipitation())
                    .category(category)
                    .effects(effects)
                    .temperatureModifier(temperatureModifier)
                    .build());
        }
    }

    /**
     * @return set of all biomes in the json file
     */
    public Set<Biome> biomes() {
        return Collections.unmodifiableSet(biomes);
    }

    @Override
    public String getName() {
        return BIOMES_FILE_NAME;
    }

    @Override
    public Optional<InputStream> getOriginal() {
        return Optional.ofNullable(Machine.CLASS_LOADER.getResourceAsStream(BIOMES_FILE_NAME));
    }

    @Override
    public String toString() {
        return getName();
    }

}
