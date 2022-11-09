package me.pesekjak.machine.world;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chunk.Chunk;
import me.pesekjak.machine.chunk.ChunkUtils;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.PacketPlayOutChangeDifficulty;
import me.pesekjak.machine.network.packets.out.PacketPlayOutWorldSpawnPosition;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.blocks.WorldBlock;
import me.pesekjak.machine.world.dimensions.DimensionType;
import me.pesekjak.machine.world.generation.Generator;
import me.pesekjak.machine.world.region.Region;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Playable world
 */
@RequiredArgsConstructor
@Getter
public abstract class World implements ServerProperty {

    private final Machine server;

    @Getter(AccessLevel.PROTECTED)
    protected final AtomicReference<WorldManager> manager = new AtomicReference<>();

    private final NamespacedKey name;
    private final UUID uuid;
    private final DimensionType dimensionType;
    private final long seed;
    private Difficulty difficulty = Difficulty.DEFAULT_DIFFICULTY;
    private Location worldSpawn;
    protected boolean loaded = false;

    public WorldManager manager() {
        return manager.get();
    }

    /**
     * @return set of all active entities in the world
     */
    public abstract Set<Entity> getEntities();

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
    public abstract void loadPlayer(Player player);

    /**
     * Removes the player from the world.
     * @param player player to remove
     */
    public abstract void unloadPlayer(Player player);

    /**
     * Spawns an entity to the world.
     * @param entity entity to spawn
     * @param location location where the entity should spawn
     */
    public abstract void spawn(Entity entity, Location location);

    /**
     * Removes the entity from the world.
     * @param entity entity to remove
     */
    public abstract void remove(Entity entity);

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
    public abstract Chunk getChunk(int chunkX, int chunkZ);

    /**
     * @param position position
     * @return chunk at given position
     */
    public Chunk getChunk(BlockPosition position) {
        return getChunk(
                ChunkUtils.getChunkCoordinate(position.getX()),
                ChunkUtils.getChunkCoordinate(position.getZ())
        );
    }

    /**
     * @param location location
     * @return chunk at given location
     */
    public Chunk getChunk(Location location) {
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
    public void setBlock(BlockType blockType, BlockPosition position, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source) {
        getChunk(position).setBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY() - dimensionType.getMinY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()),
                blockType, reason, replaceReason, source);
    }

    public void setBlock(BlockType blockType, Location location, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source) {
        setBlock(blockType, location.toBlockPosition(), reason, replaceReason, source);
    }

    public void setBlock(BlockType blockType, BlockPosition position) {
        setBlock(blockType, position, BlockType.CreateReason.SET, BlockType.DestroyReason.REMOVED, null);
    }

    public void setBlock(BlockType blockType, Location location) {
        setBlock(blockType, location, BlockType.CreateReason.SET, BlockType.DestroyReason.REMOVED, null);
    }

    /**
     * @param position position
     * @return world block at given position
     */
    public WorldBlock getBlock(BlockPosition position) {
        return getChunk(position).getBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY() - dimensionType.getMinY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()));
    }

    /**
     * @param location location
     * @return world block at given location
     */
    public WorldBlock getBlock(Location location) {
        return getBlock(location.toBlockPosition());
    }

    /**
     * Changes difficulty of the world
     * @param difficulty new difficulty
     */
    public void setDifficulty(Difficulty difficulty) {
        if(difficulty == null) return;
        this.difficulty = difficulty;
        PacketOut packet = new PacketPlayOutChangeDifficulty(difficulty);
        for(Entity entity : getEntities()) {
            if(!(entity instanceof Player player)) continue;
            player.sendPacket(packet);
        }
    }

    /**
     * Changes world spawn of the world
     * @param location new world spawn
     */
    public void setWorldSpawn(Location location) {
        if(location == null) return;
        this.worldSpawn = location;
        PacketOut packet = new PacketPlayOutWorldSpawnPosition(location);
        for(Entity entity : getEntities()) {
            if(!(entity instanceof Player player)) continue;
            player.sendPacket(packet);
        }
    }

}
