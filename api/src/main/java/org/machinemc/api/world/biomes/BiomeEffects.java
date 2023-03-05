package org.machinemc.api.world.biomes;

import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.particles.Particle;
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
    @Nullable Float getBiomeParticleProbability();

    /**
     * @return particle of the biome
     */
    @Nullable Particle getBiomeParticle();

    /**
     * Modifier of the grass color.
     */
    enum GrassColorModifier {
        NONE, DARK_FOREST, SWAMP
    }

    /**
     * Mood sound playing in the biome.
     */
    interface MoodSound extends NBTSerializable {

        /**
         * @return name of the sound
         */
        NamespacedKey sound();

        /**
         * @return the minimum delay in two plays
         */
        int tickDelay();

        /**
         * Determines the cubic range of possible positions to
         * find place to play the mood sound.
         * The player is at the center of the cubic range,
         * and the edge length is 2 * block_search_extent.
         * @return block search extent
         */
        int blockSearchExtent();

        /**
         * The higher the value makes the sound source further
         * away from the player.
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
         * @return the probability to start playing the sound
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
         * @return min delay between two music
         */
        int minDelay();

        /**
         * @return max delay between two music
         */
        int maxDelay();

        /**
         * @return if the playing music should be replaced
         */
        boolean replaceCurrentMusic();

    }

}
