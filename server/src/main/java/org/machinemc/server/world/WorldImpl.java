package org.machinemc.server.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import org.machinemc.server.world.region.Region;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a world, which may contain entities, chunks and blocks.
 */
@RequiredArgsConstructor
@Getter
public abstract class WorldImpl implements World {

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
    public void setBlock(BlockType blockType, BlockPosition position, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source) {
        getChunk(position).setBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY() - dimensionType.getMinY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                blockType, reason, replaceReason, source);
    }

    @Override
    public WorldBlock getBlock(BlockPosition position) {
        return getChunk(position).getBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY() - dimensionType.getMinY(),
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

    /**
     * @param regionX x coordinate of the region
     * @param regionZ z coordinate of the region
     * @return region at given coordinates
     */
    public abstract Region getRegion(int regionX, int regionZ);

    /**
     * Saves region at given coordinates.
     * @param regionX x coordinate of the region
     * @param regionZ z coordinate of the region
     * @throws IOException if an I/O error occurs during unloading
     */
    public abstract void saveRegion(int regionX, int regionZ) throws IOException;

    /**
     * Saves the given region.
     * @param region region to save
     * @throws IOException if an I/O error occurs during unloading
     */
    public void saveRegion(Region region) throws IOException {
        if(region.getWorld() != this) throw new IllegalStateException();
        saveRegion(region.getX(), region.getZ());
    }

}
