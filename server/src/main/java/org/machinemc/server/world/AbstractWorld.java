package org.machinemc.server.world;

import lombok.Getter;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.server.Machine;
import org.machinemc.api.entities.Entity;
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
import java.util.concurrent.atomic.AtomicReference;

import static org.machinemc.server.chunk.ChunkUtils.getSectionRelativeCoordinate;

/**
 * Represents a world, which may contain entities, chunks and blocks.
 */
@Getter
public abstract class AbstractWorld implements World {

    private final Machine server;

    protected final AtomicReference<WorldManager> managerReference = new AtomicReference<>();

    private final NamespacedKey name;
    private final UUID uuid;
    private final DimensionType dimensionType;
    private final WorldType worldType;
    private final long seed;
    private Difficulty difficulty;
    private Location worldSpawn = Location.of(0, 0, 0, this);
    protected boolean loaded = false;

    public AbstractWorld(final Machine server,
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
        PacketOut packet = new PacketPlayOutChangeDifficulty(difficulty);
        for (Entity entity : getEntities()) {
            if (!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

    @Override
    public void setWorldSpawn(final Location location) {
        this.worldSpawn = location;
        PacketOut packet = new PacketPlayOutWorldSpawnPosition(location);
        for (Entity entity : getEntities()) {
            if (!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

}
