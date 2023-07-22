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
package org.machinemc.server.world.blocks;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.Synchronized;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;

import java.util.Objects;
import java.util.function.Function;

/**
 * Manager of world blocks, prevents multiple WorldBlock instances of a single block to exists,
 * provides suppliers of block types and nbt to the world blocks.
 */
@SuppressWarnings("UnstableApiUsage")
public class WorldBlockManager {

    @Getter
    private final World world;
    private final Function<BlockPosition, BlockType> blockTypeSupplier;

    private final Cache<BlockPosition, WorldBlock> cached = CacheBuilder.newBuilder()
            .weakValues()
            .build();

    public WorldBlockManager(final World world, final Function<BlockPosition, BlockType> supplier) {
        this.world = Objects.requireNonNull(world);
        blockTypeSupplier = Objects.requireNonNull(supplier);
    }

    /**
     * Returns a world block instance of a given position.
     * @param position position
     * @return world block at given position
     */
    @Synchronized
    public WorldBlock get(final BlockPosition position) {
        try {
            return cached.get(position, () -> new WorldBlockImpl(
                    world,
                    position,
                    () -> blockTypeSupplier.apply(position)));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
