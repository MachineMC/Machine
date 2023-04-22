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
package org.machinemc.server.world.region;

import lombok.Setter;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeManager;
import org.machinemc.api.world.blocks.BlockManager;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.landscape.LandscapeHandler;

import java.util.Set;

@Setter
public class DefaultLandscapeHandler implements LandscapeHandler {

    private NamespacedKey defaultBlock;
    private NamespacedKey defaultBiome;

    private final boolean autoSave;
    private final int autoSaveLimit;

    public DefaultLandscapeHandler(final BlockManager blockManager,
                                   final BiomeManager biomeManager,
                                   final boolean autoSave,
                                   final int autoSaveLimit) {

        if (blockManager.isRegistered(NamespacedKey.minecraft("air"))) {
            defaultBlock = NamespacedKey.minecraft("air");
        } else {
            final Set<BlockType> blockTypes = blockManager.getBlocks();
            if (blockTypes.size() == 0) throw new IllegalStateException();
            for (final BlockType blockType : blockTypes) {
                if (blockType.getProperties().isAir()) {
                    defaultBlock = blockType.getName();
                    break;
                }
            }
            if (defaultBlock == null)
                defaultBlock = blockTypes.iterator().next().getName();
        }

        final Set<Biome> biomes = biomeManager.getBiomes();
        if (biomes.size() == 0) throw new IllegalStateException();
        defaultBiome = biomes.iterator().next().getName();

        this.autoSave = autoSave;
        this.autoSaveLimit = autoSaveLimit;
    }

    @Override
    public String getDefaultType() {
        return defaultBlock.toString();
    }

    @Override
    public String getDefaultBiome() {
        return defaultBiome.toString();
    }

    @Override
    public boolean isAutoSave() {
        return autoSave;
    }

    @Override
    public int getAutoSaveLimit() {
        return autoSaveLimit;
    }

}
