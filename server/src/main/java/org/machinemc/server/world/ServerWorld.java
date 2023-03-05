package org.machinemc.server.world;

import io.netty.util.collection.IntObjectHashMap;
import lombok.Getter;
import lombok.Synchronized;
import org.machinemc.server.Machine;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.server.chunk.ChunkUtils;
import org.machinemc.api.entities.Player;
import org.machinemc.api.entities.Entity;
import org.machinemc.server.utils.FileUtils;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.Location;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldType;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.server.world.blocks.BlockTypeImpl;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.server.world.generation.FlatStoneGenerator;
import org.machinemc.api.world.generation.Generator;
import org.machinemc.server.world.region.AnvilRegion;
import org.machinemc.server.world.region.Region;
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
    private final File folder;
    private final File regionFolder;
    private final IntObjectHashMap<Region> regionMap = new IntObjectHashMap<>();

    protected final Set<Entity> entityList = new CopyOnWriteArraySet<>();

    private final Generator generator = new FlatStoneGenerator(getServer(), getSeed());

    /**
     * Creates default server world.
     * @param server server
     * @return default server world
     */
    public static World createDefault(Machine server) {
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

    public ServerWorld(File folder, Machine server, NamespacedKey name, DimensionType dimensionType, WorldType worldType, long seed) {
        super(server, name, FileUtils.getOrCreateUUID(folder), dimensionType, worldType, seed);
        this.folder = folder;
        regionFolder = new File(folder.getPath() + "/region/");
    }

    @Override
    public Set<Entity> getEntities() {
        return Collections.unmodifiableSet(entityList);
    }

    @Override
    public Generator getGenerator() {
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
    public void loadPlayer(Player player) {
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
    public void unloadPlayer(Player player) {
        // TODO implement player unloading
    }

    @Override
    public boolean spawn(Entity entity, Location location) {
        entityList.add(entity); // TODO implement entity spawning
        return true;
    }

    @Override
    public boolean remove(Entity entity) {
        entityList.remove(entity); // TODO implement entity removing
        return true;
    }

    @Override
    public Region getRegion(int regionX, int regionZ) {
        return regionMap.get(createRegionIndex(regionX, regionZ));
    }

    @Override
    public void saveRegion(int regionX, int regionZ) throws IOException {
        getRegion(regionX, regionZ).save();
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
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
        Chunk chunk = region.getChunk(Math.abs((chunkX + 32) % 32), Math.abs((chunkZ + 32) % 32));
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
