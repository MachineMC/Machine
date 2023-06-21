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
package org.machinemc.server.world;

import lombok.AccessLevel;
import lombok.Getter;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.Server;
import org.machinemc.server.entities.ServerPlayer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.network.packets.out.play.PacketPlayOutChangeDifficulty;
import org.machinemc.server.network.packets.out.play.PacketPlayOutWorldSpawnPosition;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.*;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.api.world.dimensions.DimensionType;

import java.util.UUID;

import static org.machinemc.server.chunk.ChunkUtils.getSectionRelativeCoordinate;

/**
 * Represents a world, which may contain entities, chunks and blocks.
 */
@Getter
public abstract class AbstractWorld implements World {

    private final Server server;

    private final NamespacedKey name;
    @Getter(AccessLevel.NONE)
    private final UUID uuid;
    private final DimensionType dimensionType;
    private final WorldType worldType;
    private final long seed;
    private Difficulty difficulty;
    private Location worldSpawn = Location.of(0, 0, 0, this);
    protected boolean loaded = false;

    public AbstractWorld(final Server server,
                         final NamespacedKey name,
                         final UUID uuid,
                         final DimensionType dimensionType,
                         final WorldType worldType,
                         final long seed) {
        this.server = server;
        this.name = name;
        this.uuid = uuid;
        this.dimensionType = dimensionType;
        this.worldType = worldType;
        this.seed = seed;
        difficulty = server.getProperties().getDefaultDifficulty();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public WorldBlock getBlock(final BlockPosition position) {
        return getChunk(position).getBlock(
                getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                getSectionRelativeCoordinate(position.getZ()));
    }

    @Override
    public void setBlock(final BlockType blockType, final BlockPosition position) {
        getChunk(position).setBlock(
                getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                getSectionRelativeCoordinate(position.getZ()),
                blockType);
    }

    @Override
    public void setBiome(final Biome biome, final BlockPosition position) {
        getChunk(position).setBiome(
                getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                getSectionRelativeCoordinate(position.getZ()),
                biome);
    }

    @Override
    public Biome getBiome(final BlockPosition position) {
        return getChunk(position).getBiome(
                getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                getSectionRelativeCoordinate(position.getZ()));
    }

    @Override
    public void setDifficulty(final Difficulty difficulty) {
        this.difficulty = difficulty;
        final PacketOut packet = new PacketPlayOutChangeDifficulty(difficulty);
        for (final Entity entity : getEntities()) {
            if (!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

    @Override
    public void setWorldSpawn(final Location location) {
        this.worldSpawn = location;
        final PacketOut packet = new PacketPlayOutWorldSpawnPosition(location);
        for (final Entity entity : getEntities()) {
            if (!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

    @Override
    public String toString() {
        return "World("
                + "name=" + name
                + ", uuid=" + uuid
                + ')';
    }

}
