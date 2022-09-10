package me.pesekjak.machine.world.biomes;

import lombok.Builder;
import lombok.Getter;
import me.pesekjak.machine.nbt.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.particles.Particle;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Locale;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
@Builder
public class BiomeEffects implements NBTSerializable {

    public static final BiomeEffects DEFAULT_EFFECTS = BiomeEffects.builder()
            .fogColor(0xC0D8FF)
            .skyColor(0x78A7FF)
            .waterColor(0x3F76E4)
            .waterFogColor(0x50533)
            .build();

    @Getter
    private final int fogColor;
    @Getter
    private final int skyColor;
    @Getter
    private final int waterColor;
    @Getter
    private final int waterFogColor;
    @Getter
    private final int foliageColor;
    @Getter
    private final int grassColor;
    @Getter @Nullable
    private final GrassColorModifier grassColorModifier;
    @Getter @Nullable
    private final NamespacedKey ambientSound;
    @Getter @Nullable
    private final MoodSound moodSound;
    @Getter @Nullable
    private final AdditionsSound additionsSound;
    @Getter @Nullable
    private final Music music;
    @Getter
    private final int biomeParticleProbability;
    @Getter @Nullable
    private final Particle biomeParticle;

    @Override
    public NBTCompound toNBT() {
        return NBT.Compound(nbt -> {
            nbt.setInt("fog_color", fogColor);
            nbt.setInt("sky_color", skyColor);
            nbt.setInt("water_color", waterColor);
            nbt.setInt("water_fog_color", waterFogColor);
            if (foliageColor != -1)
                nbt.setInt("foliage_color", foliageColor);
            if (grassColor != -1)
                nbt.setInt("grass_color", grassColor);
            if (grassColorModifier != null)
                nbt.setString("grass_color_modifier", grassColorModifier.name().toLowerCase(Locale.ROOT));
            if (ambientSound != null)
                nbt.setString("ambient_sound", ambientSound.toString());
            if (moodSound != null)
                nbt.set("mood_sound", moodSound.toNBT());
            if (additionsSound != null)
                nbt.set("additions_sound", additionsSound.toNBT());
            if (music != null)
                nbt.set("music", music.toNBT());
            if(biomeParticle != null && biomeParticleProbability != -1)
                nbt.set("particle", NBT.Compound(Map.of(
                        "probability", NBT.Float(biomeParticleProbability),
                        "options", biomeParticle.toNBT()))
                );
        });
    }

    public enum GrassColorModifier {
        NONE, DARK_FOREST, SWAMP;
    }

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

    public record AdditionsSound(NamespacedKey sound, double tickChance) implements NBTSerializable {
        @Override
        public NBTCompound toNBT() {
            return NBT.Compound(Map.of(
                    "sound", NBT.String(sound.toString()),
                    "tick_chance", NBT.Double(tickChance)));
        }
    }

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
