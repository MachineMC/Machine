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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.landscape.Landscape;
import org.machinemc.landscape.LandscapeHandler;
import org.machinemc.server.chunk.ChunkUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Handles landscape region folder of a world.
 */
@SuppressWarnings("UnstableApiUsage")
public class LandscapeHelper {

    @Getter
    private final World source;
    private final Scheduler scheduler;
    private final File regionFolder;
    private final short height;
    @Getter
    private final LandscapeHandler handler;

    private final Cache<Long, Landscape> landscapes;

    public LandscapeHelper(final World source, final File regionFolder, final LandscapeHandler handler) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(regionFolder);
        Objects.requireNonNull(handler);
        this.source = source;
        scheduler = source.getServer().getScheduler();
        this.regionFolder = regionFolder;
        height = (short) source.getDimensionType().getHeight();
        this.handler = handler;
        landscapes = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .removalListener(notification -> Scheduler.task((input, session) -> {
                    if (notification.wasEvicted()) {
                        final Landscape landscape = (Landscape) notification.getValue();
                        assert landscape != null;
                        try {
                            landscape.flush();
                            landscape.close();
                        } catch (Exception exception) {
                            source.getServerExceptionHandler().handle(exception);
                        }
                    }
                    return null;
                }).run(scheduler))
                .build();
    }

    /**
     * Loads a landscape at given world coordinates.
     * @param x x coordinate
     * @param z z coordinate
     * @return landscape for give coordinates
     */
    public Landscape get(final int x, final int z) throws ExecutionException {
        final int chunkX = ChunkUtils.getChunkCoordinate(x);
        final int chunkZ = ChunkUtils.getChunkCoordinate(z);
        return landscapes.get(regionIndex(chunkX, chunkZ),
                () -> Landscape.of(regionFolder, chunkX >> 4, chunkZ >> 4, height, handler)
        );
    }

    /**
     * Loads a landscape at given block position.
     * @param position position
     * @return landscape for given block position
     */
    public Landscape get(final BlockPosition position) throws ExecutionException {
        Objects.requireNonNull(position);
        return get(position.getX(), position.getZ());
    }

    /**
     * Flushes all landscapes of this helper.
     */
    public void flush() {
        landscapes.asMap().values().forEach(Landscape::flush);
    }

    /**
     * Closes all landscapes of this helper.
     */
    public void close() throws IOException {
        for (final Landscape landscape : landscapes.asMap().values()) {
            landscape.flush();
            landscape.close();
        }
        landscapes.asMap().clear();
    }

    private long regionIndex(final int chunkX, final int chunkZ) {
        return ((long) (chunkX >> 4) << 32) | ((chunkZ >> 4) & 0xFFFFFFFFL);
    }

}
