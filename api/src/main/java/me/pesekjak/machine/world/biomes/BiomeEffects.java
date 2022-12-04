package me.pesekjak.machine.world.biomes;

import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.particles.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents effects in a certain biome.
 */
public interface BiomeEffects extends NBTSerializable {

    /**
     * @return fog color of the biome
     */
    int getFogColor();

    /**
     * @return sky color of the biome
     */
    int getSkyColor();

    /**
     * @return water color of the biome
     */
    int getWaterColor();

    /**
     * @return water fog color of the biome
     */
    int getWaterFogColor();

    /**
     * @return foliage color of the biome
     */
    @Nullable Integer getFoliageColor();

    /**
     * @return grass color of the biome
     */
    @Nullable Integer getGrassColor();

    /**
     * @return grass color modifier of the biome
     */
    @Nullable GrassColorModifier getGrassColorModifier();

    /**
     * @return name of the ambient sound
     */
    @Nullable NamespacedKey getAmbientSound();

    /**
     * @return mood sound of the biome
     */
    @Nullable MoodSound getMoodSound();

    /**
     * @return additions sound of the biome
     */
    @Nullable AdditionsSound getAdditionsSound();

    /**
     * @return music of the biome
     */
    @Nullable Music getMusic();

    /**
     * @return probability of biome particle playing
     */
    @Nullable Integer getBiomeParticleProbability();

    /**
     * @return particle of the biome
     */
    @Nullable Particle getBiomeParticle();

    /**
     * Modifier of the grass color.
     */
    enum GrassColorModifier {
        NONE, DARK_FOREST, SWAMP;
    }

    /**
     * Mood sound playing in the biome.
     */
    interface MoodSound extends NBTSerializable {

        /**
         * @return name of the sound
         */
        @NotNull NamespacedKey sound();

        /**
         * @return delay
         */
        int tickDelay();

        /**
         * @return block search extent
         */
        int blockSearchExtent();

        /**
         * @return offset
         */
        double offset();

    }

    /**
     * Additions sound playing in the biome.
     */
    interface AdditionsSound extends NBTSerializable {

        /**
         * @return name of the sound
         */
        NamespacedKey sound();

        /**
         * @return chance
         */
        double tickChance();

    }

    /**
     * Music playing in the biome.
     */
    interface Music extends NBTSerializable {

        /**
         * @return name of the sound
         */
        NamespacedKey sound();

        /**
         * @return min delay
         */
        int minDelay();

        /**
         * @return max delay
         */
        int maxDelay();

        /**
         * @return if the playing music should be replaced
         */
        boolean replaceCurrentMusic();

    }

}
