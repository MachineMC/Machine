package me.pesekjak.machine.world;

import io.netty.util.collection.IntObjectHashMap;
import lombok.Getter;
import lombok.Synchronized;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chunk.Chunk;
import me.pesekjak.machine.chunk.WorldChunk;
import me.pesekjak.machine.chunk.ChunkUtils;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.utils.FileUtils;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.blocks.BlockTypeImpl;
import me.pesekjak.machine.world.dimensions.DimensionType;
import me.pesekjak.machine.world.generation.FlatStoneGenerator;
import me.pesekjak.machine.world.generation.Generator;
import me.pesekjak.machine.world.region.AnvilRegion;
import me.pesekjak.machine.world.region.Region;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.mca.AnvilException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Server with a folder in the main server directory.
 */
public class ServerWorld extends WorldImpl {

    public final static String DEFAULT_WORLD_FOLDER = "level";

    @Getter
    private final @NotNull File folder;
    private final @NotNull File regionFolder;
    private final @NotNull IntObjectHashMap<Region> regionMap = new IntObjectHashMap<>();

    protected final @NotNull Set<Entity> entityList = new CopyOnWriteArraySet<>();

    private final @NotNull Generator generator = new FlatStoneGenerator(getServer(), getSeed());

    /**
     * Creates default server world.
     * @param server server
     * @return default server world
     */
    public static @NotNull World createDefault(@NotNull Machine server) {
        final World world = new ServerWorld(
                new File(DEFAULT_WORLD_FOLDER + "/"),
                server,
                NamespacedKey.machine("main"),
                server.getDimensionTypeManager().getDimensions().iterator().next(),
                server.getProperties().getDefaultWorldType(),
                1);
        world.setWorldSpawn(new Location(0, world.getDimensionType().getMinY(), 0, world));
        world.setDifficulty(server.getProperties().getDefaultDifficulty());
        return world;
    }

    public ServerWorld(@NotNull File folder, @NotNull Machine server, @NotNull NamespacedKey name, @NotNull DimensionType dimensionType, @NotNull WorldType worldType, long seed) {
        super(server, name, FileUtils.getOrCreateUUID(folder), dimensionType, worldType, seed);
        this.folder = folder;
        regionFolder = new File(folder.getPath() + "/region/");
    }

    @Override
    public @NotNull Set<Entity> getEntities() {
        return Collections.unmodifiableSet(entityList);
    }

    @Override
    public @NotNull Generator getGenerator() {
        return generator;
    }

    @Override
    @Synchronized
    public void load() throws IOException {
        if(loaded) throw new UnsupportedOperationException();
        if(!regionFolder.mkdirs() && !regionFolder.exists())
            throw new IOException();
        loaded = true;
        getServer().getConsole().info("Loaded world '" + getName() + "'");
    }

    @Override
    @Synchronized
    public void unload() throws IOException {
        if(!loaded) throw new UnsupportedOperationException();
        loaded = false;
        save();
        regionMap.clear();
        getServer().getConsole().info("Unloaded world '" + getName() + "'");
    }

    @Override
    @Synchronized
    public void save() throws IOException {
        getServer().getConsole().info("Saving world '" + getName() + "'...");
        for(Region region : regionMap.values())
            region.save();
        getServer().getConsole().info("Saved world '" + getName() + "'");
    }

    @Override
    public void loadPlayer(@NotNull Player player) {
        final byte range = 3; // (byte) getServer().getViewDistance();
        final Chunk center = getChunk(player.getLocation());
        for(int x = -range; x < range + 1; x++) {
            for(int z = -range; z < range + 1; z++) {
                Chunk chunk = getChunk(center.getChunkX() + x, center.getChunkZ() + z);
                chunk.sendChunk(player);
            }
        }
    }

    @Override
    public void unloadPlayer(@NotNull Player player) {
        // TODO implement player unloading
    }

    @Override
    public boolean spawn(@NotNull Entity entity, @NotNull Location location) {
        entityList.add(entity); // TODO implement entity spawning
        return true;
    }

    @Override
    public boolean remove(@NotNull Entity entity) {
        entityList.remove(entity); // TODO implement entity removing
        return true;
    }

    @Override
    public @NotNull Region getRegion(int regionX, int regionZ) {
        return regionMap.get(createRegionIndex(regionX, regionZ));
    }

    @Override
    public void saveRegion(int regionX, int regionZ) throws IOException {
        getRegion(regionX, regionZ).save();
    }

    @Override
    public @NotNull WorldChunk getChunk(int chunkX, int chunkZ) {
        final int regionX = ChunkUtils.getRegionCoordinate(chunkX);
        final int regionZ = ChunkUtils.getRegionCoordinate(chunkZ);
        Region region = regionMap.get(createRegionIndex(regionX, regionZ));
        if(region == null) {
            try {
                File regionFile = new File(regionFolder.getPath() + "/r." + regionX + "." + regionZ + ".mca");
                if(!regionFile.createNewFile() && !regionFile.exists())
                    throw new IllegalStateException();
                region = new AnvilRegion(this, regionFile, regionX, regionZ);
                regionMap.put(createRegionIndex(regionX, regionZ), region);
            } catch (AnvilException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        final int relativeX = Math.abs((chunkX + 32) % 32);
        final int relativeZ = Math.abs((chunkZ + 32) % 32);

        boolean generation = region.shouldGenerate(relativeX, relativeZ);
        WorldChunk chunk = region.getChunk(Math.abs((chunkX + 32) % 32), Math.abs((chunkZ + 32) % 32));
        if(!generation) return chunk;
        final int minY = getDimensionType().getMinY();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < getDimensionType().getHeight(); y++) {
                    final BlockType type = getGenerator().generate(new BlockPosition(Chunk.CHUNK_SIZE_X * chunkX + x, y + 1 + minY, Chunk.CHUNK_SIZE_Z * chunkZ + z));
                    chunk.setBlock(x, y, z, type, BlockTypeImpl.CreateReason.GENERATED, null, null);
                }
            }
        }
        return chunk;
    }

    /**
     * @param regionX x coordinate of the region
     * @param regionZ z coordinate of the region
     * @return unique index for a region at given coordinates
     */
    private int createRegionIndex(int regionX, int regionZ) {
        if(regionX > 0x7FFF || regionZ > 0x7FFF) throw new UnsupportedOperationException();
        int index = 0;
        if(regionX < 0) index |= (1 << 31);
        if(regionZ < 0) index |= (1 << 15);
        index |= (Math.abs(regionX) << 16);
        index |= Math.abs(regionZ);
        return index;
    }

}
