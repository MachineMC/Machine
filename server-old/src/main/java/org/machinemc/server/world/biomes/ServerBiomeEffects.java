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

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.particles.Particle;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.biomes.BiomeEffects;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;

import java.util.Map;
import java.util.Optional;
import java.util.Objects;

/**
 * Default biome effects implementation.
 */
@Builder
public class ServerBiomeEffects implements BiomeEffects {

    /**
     * Creates the default biome effects.
     * @return newly created biome effects
     */
    public static BiomeEffects createDefault() {
        return ServerBiomeEffects.builder()
                .build();
    }

    @Getter
    @Builder.Default private Colour fogColor = new HexColor(0xC0D8FF);
    @Getter
    @Builder.Default private Colour skyColor = new HexColor(0x78A7FF);
    @Getter
    @Builder.Default private Colour waterColor = new HexColor(0x3F76E4);
    @Getter
    @Builder.Default private Colour waterFogColor = new HexColor(0x50533);
    @Builder.Default private @Nullable Colour foliageColor = null;
    @Builder.Default private @Nullable Colour grassColor = null;
    @Builder.Default private @Nullable BiomeEffects.GrassColorModifier grassColorModifier = null;
    @Builder.Default private @Nullable NamespacedKey ambientSound = null;
    @Builder.Default private @Nullable MoodSound moodSound = null;
    @Builder.Default private @Nullable AdditionsSound additionsSound = null;
    @Builder.Default private @Nullable Music music = null;
    @Builder.Default private @Nullable Float biomeParticleProbability = null;
    @Builder.Default private @Nullable Particle<?> biomeParticle = null;

    ServerBiomeEffects(final Colour fogColor,
                       final Colour skyColor,
                       final Colour waterColor,
                       final Colour waterFogColor,
                       final @Nullable Colour foliageColor,
                       final @Nullable Colour grassColor,
                       final @Nullable BiomeEffects.GrassColorModifier grassColorModifier,
                       final @Nullable NamespacedKey ambientSound,
                       final @Nullable MoodSound moodSound,
                       final @Nullable AdditionsSound additionsSound,
                       final @Nullable Music music,
                       final @Nullable Float biomeParticleProbability,
                       final @Nullable Particle<?> biomeParticle) {
        this.fogColor = Objects.requireNonNull(fogColor, "Fog color of biome effects can not be null");
        this.skyColor = Objects.requireNonNull(skyColor, "Sky color of biome effects can not be null");
        this.waterColor = Objects.requireNonNull(waterColor, "Water color of biome effects can not be null");
        this.waterFogColor = Objects.requireNonNull(waterFogColor, "Water fog color of biome effects can not be null");
        this.foliageColor = foliageColor;
        this.grassColor = grassColor;
        this.grassColorModifier = grassColorModifier;
        this.ambientSound = ambientSound;
        this.moodSound = moodSound;
        this.additionsSound = additionsSound;
        this.music = music;
        this.biomeParticleProbability = biomeParticleProbability;
        this.biomeParticle = biomeParticle;
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound compound = new NBTCompound(Map.of(
                "fog_color", fogColor.getRGB(),
                "sky_color", skyColor.getRGB(),
                "water_color", waterColor.getRGB(),
                "water_fog_color", waterFogColor.getRGB()
        ));
        if (foliageColor != null)
            compound.set("foliage_color", foliageColor.getRGB());
        if (grassColor != null)
            compound.set("grass_color", grassColor.getRGB());
        if (grassColorModifier != null)
            compound.set("grass_color_modifier", grassColorModifier.name().toLowerCase());
        if (ambientSound != null)
            compound.set("ambient_sound", ambientSound.toString());
        if (moodSound != null)
            compound.set("mood_sound", moodSound.toNBT());
        if (additionsSound != null)
            compound.set("additions_sound", additionsSound.toNBT());
        if (music != null)
            compound.set("music", music.toNBT());
        if (biomeParticle != null && biomeParticleProbability != null)
            compound.set("particle", new NBTCompound(Map.of(
                    "probability", biomeParticleProbability,
                    "options", biomeParticle.toNBT())));
        return compound;
    }

    @Override
    public Optional<Colour> getFoliageColor() {
        return Optional.ofNullable(foliageColor);
    }

    @Override
    public Optional<Colour> getGrassColor() {
        return Optional.ofNullable(grassColor);
    }

    @Override
    public Optional<BiomeEffects.GrassColorModifier> getGrassColorModifier() {
        return Optional.ofNullable(grassColorModifier);
    }

    @Override
    public Optional<NamespacedKey> getAmbientSound() {
        return Optional.ofNullable(ambientSound);
    }

    @Override
    public Optional<BiomeEffects.MoodSound> getMoodSound() {
        return Optional.ofNullable(moodSound);
    }

    @Override
    public Optional<BiomeEffects.AdditionsSound> getAdditionsSound() {
        return Optional.ofNullable(additionsSound);
    }

    @Override
    public Optional<BiomeEffects.Music> getMusic() {
        return Optional.ofNullable(music);
    }

    @Override
    public Optional<Float> getBiomeParticleProbability() {
        return Optional.ofNullable(biomeParticleProbability);
    }

    @Override
    public Optional<Particle<?>> getBiomeParticle() {
        return Optional.ofNullable(biomeParticle);
    }

    @Override
    public String toString() {
        return "ServerBiomeEffects("
                + "fogColor=" + fogColor.getRGB()
                + ", skyColor=" + skyColor.getRGB()
                + ", waterColor=" + waterColor.getRGB()
                + ", waterFogColor=" + waterFogColor.getRGB()
                + (foliageColor != null ? ", foliageColor=" + foliageColor.getRGB() : "")
                + (grassColor != null ? ", grassColor=" + grassColor.getRGB() : "")
                + (grassColorModifier != null ? ", grassColorModifier=" + grassColorModifier : "")
                + (ambientSound != null ? ", ambientSound=" + ambientSound : "")
                + (moodSound != null ? ", moodSound=" + moodSound : "")
                + (additionsSound != null ? ", additionsSound=" + additionsSound : "")
                + (music != null ? ", music=" + music : "")
                + (biomeParticleProbability != null ? ", biomeParticleProbability=" + biomeParticleProbability : "")
                + (biomeParticle != null ? ", biomeParticle=" + biomeParticle : "")
                + ')';
    }

    /**
     * Sound playing in a biome.
     * @param sound mood sound that will play
     * @param tickDelay delay between the sounds
     * @param blockSearchExtent Determines the cubic range of possible positions
     *                          to find place to play the mood sound.
     *                          The player is at the center of the cubic range,
     *                          and the edge length is search extent times two
     * @param offset the higher the value makes the sound source further
     *               away from the player.
     */
    public record MoodSoundImpl(NamespacedKey sound,
                                int tickDelay,
                                int blockSearchExtent,
                                double offset) implements MoodSound {
        @Override
        public NBTCompound toNBT() {
            return new NBTCompound(Map.of(
                    "sound", sound.toString(),
                    "tick_delay", tickDelay,
                    "block_search_extent", blockSearchExtent,
                    "offset", offset));
        }
    }

    /**
     * Additional sound playing in a biome.
     * @param sound sound that will play
     * @param tickChance chance of the sound playing
     */
    public record AdditionsSoundImpl(NamespacedKey sound, double tickChance) implements AdditionsSound {
        @Override
        public NBTCompound toNBT() {
            return new NBTCompound(Map.of(
                    "sound", sound.toString(),
                    "tick_chance", tickChance));
        }
    }

    /**
     * Music playing in a biome.
     * @param sound sound to play
     * @param minDelay min delay between music plays
     * @param maxDelay max delay between music plays
     * @param replaceCurrentMusic whether the music should replace the music that
     *                            is currently playing for the player
     */
    public record MusicImpl(NamespacedKey sound,
                            int minDelay,
                            int maxDelay,
                            boolean replaceCurrentMusic) implements Music {
        @Override
        public NBTCompound toNBT() {
            return new NBTCompound(Map.of(
                    "sound", sound.toString(),
                    "min_delay", minDelay,
                    "max_delay", maxDelay,
                    "replace_current_music", replaceCurrentMusic));
        }
    }

}
