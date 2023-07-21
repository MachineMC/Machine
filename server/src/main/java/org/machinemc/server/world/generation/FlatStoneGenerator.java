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

import lombok.Getter;
import org.machinemc.api.Server;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.World;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.generation.GeneratedSection;
import org.machinemc.api.world.generation.Generator;
import org.machinemc.nbt.NBTCompound;

import java.util.Objects;
import java.util.Optional;

/**
 * Simple flat world stone generator.
 */
@Getter
public class FlatStoneGenerator implements Generator {

    private final Server server;
    @Getter
    private final long seed;

    private final BlockType air;
    private final BlockType stone;

    private final Biome biome;

    public FlatStoneGenerator(final Server server, final long seed) {
        this.server = Objects.requireNonNull(server);
        this.seed = seed;
        final BlockManager manager = server.getBlockManager();
        this.air = manager.getBlockType(NamespacedKey.minecraft("air")).orElseThrow(() ->
                new NullPointerException("Air block type is missing in the server block manager"));
        this.stone = manager.getBlockType(NamespacedKey.minecraft("stone")).orElseThrow(() ->
                new NullPointerException("Stone block type is missing in the server block manager"));
        this.biome = server.getBiome(NamespacedKey.minecraft("plains"))
                .or(() -> Optional.ofNullable(server.getBiomeManager().getBiomes().stream().iterator().next()))
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public GeneratedSection populateChunk(final int chunkX,
                                          final int chunkZ,
                                          final int sectionIndex,
                                          final World world) {
        Objects.requireNonNull(world);
        return new GeneratedSectionImpl(
                new BlockType[]{sectionIndex > 4 ? air : stone},
                new short[GeneratedSection.BLOCK_DATA_SIZE],
                new Biome[]{biome},
                new short[GeneratedSection.BLOCK_DATA_SIZE],
                new NBTCompound[GeneratedSection.BLOCK_DATA_SIZE]);
    }

}
