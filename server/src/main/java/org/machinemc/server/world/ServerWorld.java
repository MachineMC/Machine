package org.machinemc.server.world;

import com.google.common.cache.Cache;
import lombok.Getter;
import lombok.Synchronized;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chunk.Section;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.api.utils.LazyNamespacedKey;
import org.machinemc.api.world.*;
import org.machinemc.api.world.blocks.*;
import org.machinemc.landscape.Landscape;
import org.machinemc.landscape.Segment;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.entities.Player;
import org.machinemc.api.entities.Entity;
import org.machinemc.server.chunk.ChunkUtils;
import org.machinemc.server.chunk.SectionImpl;
import org.machinemc.server.utils.FileUtils;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.server.utils.WeaklyTimedCache;
import org.machinemc.server.world.blocks.WorldBlockManager;
import org.machinemc.api.world.generation.Generator;
import org.machinemc.server.world.generation.StonePyramidGenerator;
import org.machinemc.server.world.region.DefaultLandscapeHandler;
import org.machinemc.server.world.region.LandscapeChunk;
import org.machinemc.server.world.region.LandscapeHelper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * Server with a folder in the main server directory.
 */
@SuppressWarnings("UnstableApiUsage")
public class ServerWorld extends AbstractWorld {

    public final static String DEFAULT_WORLD_FOLDER = "level";

    @Getter
    private final File folder;
    private final File regionFolder;

    protected final Set<Entity> entityList = new CopyOnWriteArraySet<>();

    private final Generator generator = new StonePyramidGenerator(getServer(), getSeed());

    private final LandscapeHelper landscapeHelper;
    @Getter
    private final WorldBlockManager worldBlockManager;

    private final Cache<Long, LandscapeChunk> cachedChunks = new WeaklyTimedCache<>(16, TimeUnit.SECONDS); // TODO configurable (check landscape chunk too, should be the same value)

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
        landscapeHelper = new LandscapeHelper(this,
                regionFolder,
                new DefaultLandscapeHandler(
                        server.getBlockManager(),
                        server.getBiomeManager(),
                        false,
                        256)
        ); // TODO auto save should be configurable
        worldBlockManager = new WorldBlockManager(this,
                (position -> {
                    final Segment segment = getSegment(position);
                    BlockType blockType = server.getBlockType(LazyNamespacedKey.lazy(segment.getBlock(position.getX() % 16, position.getY() % 16, position.getZ() % 16)));
                    if (blockType == null) {
                        blockType = server.getBlockManager().getBlockType(LazyNamespacedKey.lazy(landscapeHelper.getHandler().getDefaultType()));
                        if (blockType == null) throw new IllegalStateException();
                    }
                    return blockType;
                }),
                (position -> {
                    final Segment segment = getSegment(position);
                    return segment.getNBT(position.getX() % 16, position.getY() % 16, position.getZ() % 16);
                })
        );
    }

    private Segment getSegment(BlockPosition position) {
        try {
            final Landscape landscape = landscapeHelper.get(position.getX(), position.getZ());
            final int segmentX = Math.abs(ChunkUtils.getChunkCoordinate(position.getX())) % 16;
            final int segmentZ = Math.abs(ChunkUtils.getChunkCoordinate(position.getZ())) % 16;
            final int segmentY = (position.getY() - getDimensionType().getMinY()) / 16;
            return landscape.loadSegment(segmentX, segmentY, segmentZ);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
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
    public void load() {
        if(loaded) throw new UnsupportedOperationException();
        if(!regionFolder.mkdirs() && !regionFolder.exists())
            throw new IllegalStateException();
        loaded = true;
        getServer().getConsole().info("Loaded world '" + getName() + "'");
    }

    @Override
    @Synchronized
    public void unload() throws IOException {
        if(!loaded) throw new UnsupportedOperationException();
        loaded = false;
        save();
        landscapeHelper.close();
        getServer().getConsole().info("Unloaded world '" + getName() + "'");
    }

    @Override
    @Synchronized
    public void save() {
        getServer().getConsole().info("Saving world '" + getName() + "'...");
        landscapeHelper.flush();
        getServer().getConsole().info("Saved world '" + getName() + "'");
    }

    @Override
    public void loadPlayer(final Player player) {
        // TODO this should take player's view distance
        final long now = System.currentTimeMillis();
        final Scheduler scheduler = getServer().getScheduler();
        final int chunksPerTask = 6;
        final int tasks = 50;
        for (int i = 0; i <= tasks; i++) {
            final int index = i;
            Scheduler.task(((input, session) -> {
                final int start = index * chunksPerTask;
                final int end = (index+1) * chunksPerTask;
                for (int j = start; j < end; j++) {
                    final int[] coordinates = getSpiralCoordinates(j);
                    final Chunk chunk = getChunk(coordinates[0], coordinates[1]);
                    chunk.sendChunk(player);
                }
                if(index == tasks)
                    player.sendMessage(Component.text("Loading of " + (tasks*chunksPerTask) + " chunks took " + (System.currentTimeMillis() - now) + "ms"));

                return null;
            })).async().run(scheduler);
        }
    }

    private static int[] getSpiralCoordinates(int orderIndex) {
        int x = 0;
        int y = 0;
        int currentOrder = 1;
        int currentLength = 1;
        int currentDirection = 0;

        while (currentOrder < orderIndex) {
            for (int i = 0; i < currentLength; i++) {
                if (currentOrder == orderIndex) {
                    break;
                }
                if (currentDirection == 0) {
                    x++;
                } else if (currentDirection == 1) {
                    y++;
                } else if (currentDirection == 2) {
                    x--;
                } else if (currentDirection == 3) {
                    y--;
                }
                currentOrder++;
            }
            if (currentDirection == 1 || currentDirection == 3) {
                currentLength++;
            }
            currentDirection = (currentDirection + 1) % 4;
        }

        return new int[] { x, y };
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
    public Chunk getChunk(int chunkX, int chunkZ) {
        try {
            final long chunkIndex = chunkIndex(chunkX, chunkZ);
            final LandscapeChunk chunk = cachedChunks.get(chunkIndex, () -> new LandscapeChunk(this, worldBlockManager, chunkX, chunkZ, landscapeHelper));

            for (int i = 0; i <= chunk.getMaxSection(); i++) {
                final int ry = getDimensionType().getMinY() + Chunk.CHUNK_SECTION_SIZE * i;
                final Segment segment = chunk.getSegment(i);
                if (!segment.isEmpty()) continue;

                final Generator.SectionContent content = generator.populateChunk(chunkX, chunkZ, i, this);
                final BlockType[] palette = content.getPalette();

                assert palette.length != 0;

                final short[] blocks = content.getData();
                final NBTCompound[] tileEntities = content.getTileEntitiesData();

                final Section section = new SectionImpl();

                if(palette.length != 1) {
                    segment.setAllBlocks((x, y, z) -> {
                        final int blockIndex = Generator.SectionContent.index(x, y, z);
                        final BlockType blockType = palette[blocks[blockIndex]];
                        final BlockPosition position = new BlockPosition(Chunk.CHUNK_SIZE_X * chunkX + x, ry + y, Chunk.CHUNK_SIZE_Z * chunkZ + z);

                        if (blockType instanceof EntityBlockType entityBlockType) {
                            segment.setNBT(x, y, z, initializeTileEntity(
                                    entityBlockType,
                                    position,
                                    tileEntities[blockIndex]));
                        }

                        BlockData visual;
                        if(blockType.hasDynamicVisual()) {
                            final WorldBlock.State state = new WorldBlock.State(
                                    this,
                                    position,
                                    blockType,
                                    segment.getNBT(x, y, z).clone());
                            visual = blockType.getBlockData(state);
                            for(final BlockHandler blockHandler : blockType.getHandlers())
                                visual = blockHandler.onVisualRequest(state, visual);
                        } else {
                            visual = blockType.getBlockData(null);
                        }
                        section.getBlockPalette().set(x, y, z, visual.getId());

                        return blockType.getName().toString();
                    });
                } else {
                    final BlockType blockType = palette[0];
                    segment.fill(blockType.getName().toString());

                    if (blockType instanceof EntityBlockType entityBlockType) {
                        segment.setAllNBT((x, y, z) -> {
                            final BlockPosition position = new BlockPosition(Chunk.CHUNK_SIZE_X * chunkX + x, ry + y, Chunk.CHUNK_SIZE_Z * chunkZ + z);
                            return initializeTileEntity(entityBlockType, position, tileEntities[Generator.SectionContent.index(x, y, z)]);
                        });
                    }

                    if(blockType.hasDynamicVisual()) {
                        segment.getAllNBT((x, y, z, nbt) -> {
                            final WorldBlock.State state = new WorldBlock.State(this,
                                    new BlockPosition(Chunk.CHUNK_SIZE_X * chunkX + x, ry + y, Chunk.CHUNK_SIZE_Z * chunkZ + z),
                                    blockType,
                                    segment.getNBT(x, y, z).clone());
                            BlockData visual = blockType.getBlockData(state);
                            for(final BlockHandler blockHandler : blockType.getHandlers())
                                visual = blockHandler.onVisualRequest(state, visual);
                            section.getBlockPalette().set(x, y, z, visual.getId()
                            );
                        });
                    } else {
                        section.getBlockPalette().fill(blockType.getBlockData(null).getId());
                    }

                }

                chunk.readSectionBiomeData(section, segment); // TODO should be from generator once generators support biomes
                chunk.setSection(i, section);
                segment.push(); // TODO should be configurable (saving of generated chunks not touched by player)
            }
            return chunk;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Initialize NBT compound for a tile entity generation.
     * @param entityBlockType type of the tile entity
     * @param position position of the block being generated
     * @param generatorData compound data for the tile entity provided by the generator
     * @return nbt compound for the tile entity
     */
    private NBTCompound initializeTileEntity(EntityBlockType entityBlockType, BlockPosition position, @Nullable NBTCompound generatorData) {
        final WorldBlock.State state = new WorldBlock.State(this, position, entityBlockType, new NBTCompound());
        entityBlockType.initialize(state);
        for (BlockHandler handler : entityBlockType.getHandlers())
            handler.onGeneration(state);
        if (generatorData != null)
            state.compound().putAll(generatorData);
        return state.compound();
    }

    /**
     * @param chunkX x coordinate of the chunk
     * @param chunkZ z coordinate of the chunk
     * @return unique index for a chunk at given coordinates
     */
    private long chunkIndex(final int chunkX, final int chunkZ) {
        return ((long)chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

}
