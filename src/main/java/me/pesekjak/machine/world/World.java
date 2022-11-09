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
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public abstract class World implements ServerProperty {

    @Getter
    private final Machine server;

    @Getter(AccessLevel.PROTECTED)
    protected final AtomicReference<WorldManager> manager = new AtomicReference<>();

    @Getter
    private final NamespacedKey name;
    @Getter
    private final DimensionType dimensionType;
    @Getter
    private final long seed;
    @Getter
    private Difficulty difficulty = Difficulty.DEFAULT_DIFFICULTY;
    @Getter
    private Location worldSpawn;
    @Getter
    protected boolean loaded = false;

    public WorldManager manager() {
        return manager.get();
    }

    public abstract Set<Entity> getEntities();

    public abstract Generator getGenerator();

    public abstract void load();

    public abstract void unload();

    public abstract void save();

    public abstract void loadPlayer(Player player);

    public abstract void unloadPlayer(Player player);

    public abstract void spawn(Entity entity, Location location);

    public abstract void remove(Entity entity);

    public abstract Region getRegion(int regionX, int regionZ);

    public abstract void saveRegion(int regionX, int regionZ);

    public void saveRegion(Region region) {
        if(region.getWorld() != this) throw new IllegalStateException();
        saveRegion(region.getX(), region.getZ());
    }

    public abstract Chunk getChunk(int chunkX, int chunkZ);

    public Chunk getChunk(BlockPosition position) {
        return getChunk(
                ChunkUtils.getChunkCoordinate(position.getX()),
                ChunkUtils.getChunkCoordinate(position.getZ())
        );
    }

    public Chunk getChunk(Location location) {
        return getChunk(location.toBlockPosition());
    }

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

    public WorldBlock getBlock(BlockPosition position) {
        return getChunk(position).getBlock(
                ChunkUtils.getSectionRelativeCoordinate(position.getX()),
                position.getY() - dimensionType.getMinY(),
                ChunkUtils.getSectionRelativeCoordinate(position.getZ()));
    }

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
