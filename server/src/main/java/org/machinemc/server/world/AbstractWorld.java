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

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
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

import java.util.Locale;
import java.util.Objects;
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
    private EntityPosition worldSpawn;
    protected boolean loaded = false;

    public AbstractWorld(final Server server,
                         final NamespacedKey name,
                         final UUID uuid,
                         final DimensionType dimensionType,
                         final WorldType worldType,
                         final long seed,
                         final @Nullable Difficulty difficulty,
                         final EntityPosition worldSpawn) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
        this.name = Objects.requireNonNull(name, "Name can not be null");
        this.uuid = Objects.requireNonNull(uuid, "UUID can not be null");
        this.dimensionType = Objects.requireNonNull(dimensionType, "Dimension type can not be null");
        this.worldType = Objects.requireNonNull(worldType, "World type can not be null");
        this.seed = seed;
        this.difficulty = difficulty != null ? difficulty : server.getProperties().getDefaultDifficulty();
        this.worldSpawn = worldSpawn;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public WorldBlock getBlock(final BlockPosition position) {
        Objects.requireNonNull(position, "Block position can not be null");
        return getChunk(position).getBlock(
                getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                getSectionRelativeCoordinate(position.getZ()));
    }

    @Override
    public void setBlock(final BlockType blockType, final BlockPosition position) {
        Objects.requireNonNull(blockType, "Block type can not be null");
        Objects.requireNonNull(position, "Block position can not be null");
        getChunk(position).setBlock(
                getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                getSectionRelativeCoordinate(position.getZ()),
                blockType);
    }

    @Override
    public void setBiome(final Biome biome, final BlockPosition position) {
        Objects.requireNonNull(biome, "Biome can not be null");
        Objects.requireNonNull(position, "Block position can not be null");
        getChunk(position).setBiome(
                getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                getSectionRelativeCoordinate(position.getZ()),
                biome);
    }

    @Override
    public Biome getBiome(final BlockPosition position) {
        Objects.requireNonNull(position, "Block position can not be null");
        return getChunk(position).getBiome(
                getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                getSectionRelativeCoordinate(position.getZ()));
    }

    @Override
    public void setDifficulty(final Difficulty difficulty) {
        this.difficulty = Objects.requireNonNull(difficulty, "Difficulty can not be null");
        final PacketOut packet = new PacketPlayOutChangeDifficulty(difficulty);
        for (final Entity entity : getEntities()) {
            if (!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

    @Override
    public void setWorldSpawn(final EntityPosition position) {
        this.worldSpawn = Objects.requireNonNull(position, "World spawn can not be null");
        final PacketOut packet = new PacketPlayOutWorldSpawnPosition(position);
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
