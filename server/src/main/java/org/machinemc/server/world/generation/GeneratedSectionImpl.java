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
package org.machinemc.server.world.generation;

import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.generation.GeneratedSection;
import org.machinemc.nbt.NBTCompound;

import java.util.Objects;

/**
 * Default implementation of the generated section.
 * @param blockPalette palette of used blocks
 * @param blockData block data
 * @param biomePalette palette of used biomes
 * @param biomeData biome data
 * @param tileEntitiesData data for tile entities
 */
public record GeneratedSectionImpl(BlockType[] blockPalette,
                                   short[] blockData,
                                   Biome[] biomePalette,
                                   short[] biomeData,
                                   NBTCompound[] tileEntitiesData) implements GeneratedSection {

    public GeneratedSectionImpl {
        Objects.requireNonNull(blockPalette);
        Objects.requireNonNull(blockData);
        Objects.requireNonNull(biomePalette);
        Objects.requireNonNull(biomeData);
        Objects.requireNonNull(tileEntitiesData);
    }

    @Override
    public BlockType[] getBlockPalette() {
        return blockPalette;
    }

    @Override
    public short[] getBlockData() {
        return blockData;
    }

    @Override
    public Biome[] getBiomePalette() {
        return biomePalette;
    }

    @Override
    public short[] getBiomeData() {
        return biomeData;
    }

    @Override
    public NBTCompound[] getTileEntitiesData() {
        return tileEntitiesData;
    }

}
