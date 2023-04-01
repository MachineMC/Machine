package org.machinemc.server.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.server.Machine;
import org.machinemc.server.chunk.ChunkUtils;
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

/**
 * Represents a world, which may contain entities, chunks and blocks.
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractWorld implements World {

    private final Machine server;

    protected final AtomicReference<WorldManager> managerReference = new AtomicReference<>();

    private final NamespacedKey name;
    private final UUID uuid;
    private final DimensionType dimensionType;
    private final WorldType worldType;
    private final long seed;
    private Difficulty difficulty = Difficulty.DEFAULT_DIFFICULTY;
    private Location worldSpawn = Location.of(0, 0, 0, this);
    protected boolean loaded = false;

    @Override
    public WorldBlock getBlock(BlockPosition position) {
        return getChunk(position).getBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()));
    }

    @Override
    public void setBlock(BlockType blockType, BlockPosition position) {
        getChunk(position).setBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                blockType);
    }

    @Override
    public void setBiome(Biome biome, BlockPosition position) {
        getChunk(position).setBiome(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                biome);
    }

    @Override
    public Biome getBiome(BlockPosition position) {
        return getChunk(position).getBiome(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()));
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        PacketOut packet = new PacketPlayOutChangeDifficulty(difficulty);
        for(Entity entity : getEntities()) {
            if(!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

    @Override
    public void setWorldSpawn(Location location) {
        this.worldSpawn = location;
        PacketOut packet = new PacketPlayOutWorldSpawnPosition(location);
        for(Entity entity : getEntities()) {
            if(!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

}
