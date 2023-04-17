package org.machinemc.server.world.biomes;

import lombok.Builder;
import lombok.Getter;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.biomes.BiomeEffects;
import org.machinemc.api.world.particles.Particle;
import org.jetbrains.annotations.Nullable;
import org.machinemc.nbt.NBTCompound;

import java.util.Map;

/**
 * Default biome effects implementation.
 */
@Getter
@Builder
public class BiomeEffectsImpl implements BiomeEffects {

    /**
     * Creates the default biome effects.
     * @return newly created biome effects
     */
    public static BiomeEffects createDefault() {
        return BiomeEffectsImpl.builder()
                .build();
    }

    @Builder.Default private final int fogColor = 0xC0D8FF;
    @Builder.Default private final int skyColor = 0x78A7FF;
    @Builder.Default private final int waterColor = 0x3F76E4;
    @Builder.Default private final int waterFogColor = 0x50533;
    private final @Nullable Integer foliageColor;
    private final @Nullable Integer grassColor;
    private final @Nullable BiomeEffects.GrassColorModifier grassColorModifier;
    private final @Nullable NamespacedKey ambientSound;
    private final @Nullable MoodSound moodSound;
    private final @Nullable AdditionsSound additionsSound;
    private final @Nullable Music music;
    private final @Nullable Float biomeParticleProbability;
    private final @Nullable Particle biomeParticle;

    @Override
    public NBTCompound toNBT() {
        NBTCompound compound = new NBTCompound(Map.of(
                "fog_color", fogColor,
                "sky_color", skyColor,
                "water_color", waterColor,
                "water_fog_color", waterFogColor
        ));
        if (foliageColor != null)
            compound.set("foliage_color", foliageColor);
        if (grassColor != null)
            compound.set("grass_color", grassColor);
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
