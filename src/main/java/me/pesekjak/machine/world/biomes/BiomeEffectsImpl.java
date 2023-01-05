package me.pesekjak.machine.world.biomes;

import lombok.Builder;
import lombok.Getter;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.particles.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

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
    public static @NotNull BiomeEffects createDefault() {
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
    private final @Nullable Integer biomeParticleProbability;
    private final @Nullable Particle biomeParticle;

    @Override
    public @NotNull NBTCompound toNBT() {
        return NBT.Compound(nbt -> {
            nbt.setInt("fog_color", fogColor);
            nbt.setInt("sky_color", skyColor);
            nbt.setInt("water_color", waterColor);
            nbt.setInt("water_fog_color", waterFogColor);
            if (foliageColor != null)
                nbt.setInt("foliage_color", foliageColor);
            if (grassColor != null)
                nbt.setInt("grass_color", grassColor);
            if (grassColorModifier != null)
                nbt.setString("grass_color_modifier", grassColorModifier.name().toLowerCase());
            if (ambientSound != null)
                nbt.setString("ambient_sound", ambientSound.toString());
            if (moodSound != null)
                nbt.set("mood_sound", moodSound.toNBT());
            if (additionsSound != null)
                nbt.set("additions_sound", additionsSound.toNBT());
            if (music != null)
                nbt.set("music", music.toNBT());
            if(biomeParticle != null && biomeParticleProbability != null)
                nbt.set("particle", NBT.Compound(Map.of(
                        "probability", NBT.Float(biomeParticleProbability),
                        "options", biomeParticle.toNBT()))
                );
        });
    }

    /**
     * Sound playing in a biome
     */
    public record MoodSoundImpl(@NotNull NamespacedKey sound, int tickDelay, int blockSearchExtent, double offset) implements MoodSound {
        @Override
        public @NotNull NBTCompound toNBT() {
            return NBT.Compound(Map.of(
                    "sound", NBT.String(sound.toString()),
                    "tick_delay", NBT.Int(tickDelay),
                    "block_search_extent", NBT.Int(blockSearchExtent),
                    "offset", NBT.Double(offset)));
        }
    }

    /**
     * Additional sound playing in a biome
     */
    public record AdditionsSoundImpl(@NotNull NamespacedKey sound, double tickChance) implements AdditionsSound {
        @Override
        public @NotNull NBTCompound toNBT() {
            return NBT.Compound(Map.of(
                    "sound", NBT.String(sound.toString()),
                    "tick_chance", NBT.Double(tickChance)));
        }
    }

    /**
     * Music playing in a biome
     */
    public record MusicImpl(@NotNull NamespacedKey sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) implements Music {
        @Override
        public @NotNull NBTCompound toNBT() {
            return NBT.Compound(Map.of(
                    "sound", NBT.String(sound.toString()),
                    "min_delay", NBT.Int(minDelay),
                    "max_delay", NBT.Int(maxDelay),
                    "replace_current_music", NBT.Boolean(replaceCurrentMusic)));
        }
    }

}
