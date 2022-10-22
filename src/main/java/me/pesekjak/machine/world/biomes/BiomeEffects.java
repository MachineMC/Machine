package me.pesekjak.machine.world.biomes;

import lombok.Builder;
import lombok.Getter;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.particles.Particle;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;

/**
 * Effects of a biome.
 */
@Getter
@Builder
public class BiomeEffects implements NBTSerializable {

    public static BiomeEffects createDefault() {
        return BiomeEffects.builder()
                .fogColor(0xC0D8FF)
                .skyColor(0x78A7FF)
                .waterColor(0x3F76E4)
                .waterFogColor(0x50533)
                .build();
    }

    @Builder.Default private final int fogColor = 0xC0D8FF;
    @Builder.Default private final int skyColor = 0x78A7FF;
    @Builder.Default private final int waterColor = 0x3F76E4;
    @Builder.Default private final int waterFogColor = 0x50533;
    private final Integer foliageColor;
    private final Integer grassColor;
    @Nullable
    private final GrassColorModifier grassColorModifier;
    @Nullable
    private final NamespacedKey ambientSound;
    @Nullable
    private final MoodSound moodSound;
    @Nullable
    private final AdditionsSound additionsSound;
    @Nullable
    private final Music music;
    private final Integer biomeParticleProbability;
    @Nullable
    private final Particle biomeParticle;

    @Override
    public NBTCompound toNBT() {
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
     * Grass color modifier of a biome.
     */
    public enum GrassColorModifier {
        NONE, DARK_FOREST, SWAMP;
    }

    /**
     * Sound playing in a biome
     */
    public record MoodSound(NamespacedKey sound, int tickDelay, int blockSearchExtent, double offset) implements NBTSerializable {
        @Override
        public NBTCompound toNBT() {
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
    public record AdditionsSound(NamespacedKey sound, double tickChance) implements NBTSerializable {
        @Override
        public NBTCompound toNBT() {
            return NBT.Compound(Map.of(
                    "sound", NBT.String(sound.toString()),
                    "tick_chance", NBT.Double(tickChance)));
        }
    }

    /**
     * Music playing in a biome
     */
    public record Music(NamespacedKey sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) implements NBTSerializable {
        @Override
        public NBTCompound toNBT() {
            return NBT.Compound(Map.of(
                    "sound", NBT.String(sound.toString()),
                    "min_delay", NBT.Int(minDelay),
                    "max_delay", NBT.Int(maxDelay),
                    "replace_current_music", NBT.Boolean(replaceCurrentMusic)));
        }
    }

}
