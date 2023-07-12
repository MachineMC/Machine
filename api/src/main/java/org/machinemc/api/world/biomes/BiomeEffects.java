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
package org.machinemc.api.world.biomes;

import org.machinemc.api.particles.Particle;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;

import java.util.Optional;
import org.machinemc.scriptive.style.Colour;

/**
 * Represents effects in a certain biome.
 */
public interface BiomeEffects extends NBTSerializable {

    /**
     * @return fog color of the biome
     */
    Colour getFogColor();

    /**
     * @return sky color of the biome
     */
    Colour getSkyColor();

    /**
     * @return water color of the biome
     */
    Colour getWaterColor();

    /**
     * @return water fog color of the biome
     */
    Colour getWaterFogColor();

    /**
     * @return foliage color of the biome
     */
    Optional<Colour> getFoliageColor();

    /**
     * @return grass color of the biome
     */
    Optional<Colour> getGrassColor();

    /**
     * @return grass color modifier of the biome
     */
    Optional<GrassColorModifier> getGrassColorModifier();

    /**
     * @return name of the ambient sound
     */
    Optional<NamespacedKey> getAmbientSound();

    /**
     * @return mood sound of the biome
     */
    Optional<MoodSound> getMoodSound();

    /**
     * @return additions sound of the biome
     */
    Optional<AdditionsSound> getAdditionsSound();

    /**
     * @return music of the biome
     */
    Optional<Music> getMusic();

    /**
     * @return probability of biome particle playing
     */
    Optional<Float> getBiomeParticleProbability();

    /**
     * @return particle of the biome
     */
    Optional<Particle<?>> getBiomeParticle();

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
