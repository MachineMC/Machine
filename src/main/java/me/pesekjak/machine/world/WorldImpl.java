package me.pesekjak.machine.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chunk.WorldChunk;
import me.pesekjak.machine.chunk.ChunkUtils;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.ServerPlayer;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.play.PacketPlayOutChangeDifficulty;
import me.pesekjak.machine.network.packets.out.play.PacketPlayOutWorldSpawnPosition;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.blocks.WorldBlock;
import me.pesekjak.machine.world.blocks.WorldBlockImpl;
import me.pesekjak.machine.world.dimensions.DimensionType;
import me.pesekjak.machine.world.generation.Generator;
import me.pesekjak.machine.world.region.Region;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Playable world
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
    private Location worldSpawn;
    protected boolean loaded = false;

    /**
     * @return set of all active entities in the world
     */
    public abstract @NotNull Set<Entity> getEntities();

    /**
     * @return generator of the world
     */
    public abstract Generator getGenerator();

    /**
     * Loads the world.
     */
    public abstract void load();

    /**
     * Unloads the world.
     */
    public abstract void unload();

    /**
     * Saves the world and its regions.
     */
    public abstract void save();

    /**
     * Loads the player in to the world.
     * @param player player to load
     */
    public abstract void loadPlayer(@NotNull Player player);

    /**
     * Removes the player from the world.
     * @param player player to remove
     */
    public abstract void unloadPlayer(@NotNull Player player);

    /**
     * Spawns an entity to the world.
     * @param entity entity to spawn
     * @param location location where the entity should spawn
     */
    public abstract boolean spawn(@NotNull Entity entity, @NotNull Location location);

    /**
     * Removes the entity from the world.
     * @param entity entity to remove
     */
    public abstract boolean remove(@NotNull Entity entity);

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
     */
    public abstract void saveRegion(int regionX, int regionZ);

    /**
     * Saves the given region.
     * @param region region to save
     */
    public void saveRegion(Region region) {
        if(region.getWorld() != this) throw new IllegalStateException();
        saveRegion(region.getX(), region.getZ());
    }

    /**
     * @param chunkX x coordinate of the chunk
     * @param chunkZ z coordinate of the chunk
     * @return chunk at given coordinates
     */
    public abstract WorldChunk getChunk(int chunkX, int chunkZ);

    /**
     * @param position position
     * @return chunk at given position
     */
    public WorldChunk getChunk(BlockPosition position) {
        return getChunk(
                ChunkUtils.getChunkCoordinate(position.getX()),
                ChunkUtils.getChunkCoordinate(position.getZ())
        );
    }

    /**
     * @param location location
     * @return chunk at given location
     */
    public WorldChunk getChunk(Location location) {
        return getChunk(location.toBlockPosition());
    }

    /**
     * Sets a world block at given location to a different block type.
     * @param blockType new block type
     * @param position position of the block
     * @param reason reason why the block type was set
     * @param replaceReason reason why the previous block type was removed
     * @param source source of the change
     */
    public void setBlock(@NotNull BlockType blockType, @NotNull BlockPosition position, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source) {
        getChunk(position).setBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY() - dimensionType.getMinY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                blockType, reason, replaceReason, source);
    }

    /**
     * @param position position
     * @return world block at given position
     */
    public @NotNull WorldBlock getBlock(@NotNull BlockPosition position) {
        return getChunk(position).getBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY() - dimensionType.getMinY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()));
    }

    /**
     * Changes difficulty of the world
     * @param difficulty new difficulty
     */
    public void setDifficulty(@NotNull Difficulty difficulty) {
        if(difficulty == null) return;
        this.difficulty = difficulty;
        PacketOut packet = new PacketPlayOutChangeDifficulty(difficulty);
        for(Entity entity : getEntities()) {
            if(!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

    /**
     * Changes world spawn of the world
     * @param location new world spawn
     */
    public void setWorldSpawn(@NotNull Location location) {
        if(location == null) return;
        this.worldSpawn = location;
        PacketOut packet = new PacketPlayOutWorldSpawnPosition(location);
        for(Entity entity : getEntities()) {
            if(!(entity instanceof ServerPlayer player)) continue;
            player.sendPacket(packet);
        }
    }

}
