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
import org.machinemc.api.chunk.Section;
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
 * Generator that generates stone pyramids.
 */
public class StonePyramidGenerator implements Generator {

    private final Server server;
    @Getter
    private final long seed;

    private final BlockType air;
    private final BlockType stone;
    private final BlockType sign;

    private final Biome biome;

    public StonePyramidGenerator(final Server server, final long seed) {
        this.server = Objects.requireNonNull(server);
        this.seed = seed;
        final BlockManager manager = server.getBlockManager();
        this.air = manager.getBlockType(NamespacedKey.minecraft("air")).orElseThrow(() ->
                new NullPointerException("Air block type is missing in the server block manager"));
        this.stone = manager.getBlockType(NamespacedKey.minecraft("stone")).orElseThrow(() ->
                new NullPointerException("Stone block type is missing in the server block manager"));
        this.sign = manager.getBlockType(NamespacedKey.minecraft("oak_sign")).orElseThrow(() ->
                new NullPointerException("Sign block type is missing in the server block manager"));
        this.biome = server.getBiome(NamespacedKey.minecraft("plains"))
                .or(() -> Optional.ofNullable(server.getBiomeManager().getBiomes().stream().iterator().next()))
                .orElseThrow(() ->
                        new NullPointerException("There are no available biomes in the server's biome manager"));
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public GeneratedSection populateChunk(final int chunkX,
                                          final int chunkZ,
                                          final int sectionIndex,
                                          final World world) {
        Objects.requireNonNull(world);
        if (sectionIndex < 4) {
            return new GeneratedSectionImpl(
                    new BlockType[]{stone},
                    new short[GeneratedSection.BLOCK_DATA_SIZE],
                    new Biome[]{biome},
                    new short[GeneratedSection.BLOCK_DATA_SIZE],
                    new NBTCompound[GeneratedSection.BLOCK_DATA_SIZE]);
        }
        if (sectionIndex > 4) {
            return new GeneratedSectionImpl(
                    new BlockType[]{air},
                    new short[GeneratedSection.BLOCK_DATA_SIZE],
                    new Biome[]{biome},
                    new short[GeneratedSection.BLOCK_DATA_SIZE],
                    new NBTCompound[GeneratedSection.BLOCK_DATA_SIZE]);
        }
        final BlockType[] palette = new BlockType[]{air, stone, sign};
        final short[] data = new short[GeneratedSection.BLOCK_DATA_SIZE];

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final int h = Math.abs(x - 8) + Math.abs(z - 8) - 1;
                for (int y = 0; y < 16; y++) {
                    data[Section.index(x, y, z)] = (short) (y < h ? 1 : 0);
                }
            }
        }
        data[Section.index(0, 15, 0)] = 2;

        return new GeneratedSectionImpl(palette, data,
                new Biome[]{biome},
                new short[GeneratedSection.BLOCK_DATA_SIZE],
                new NBTCompound[GeneratedSection.BLOCK_DATA_SIZE]);
    }

}
