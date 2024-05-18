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

import com.google.common.cache.Cache;
import lombok.Getter;
import lombok.Synchronized;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.Server;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.chunk.Section;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.Player;
import org.machinemc.api.server.schedule.Scheduler;
import org.machinemc.api.utils.LazyNamespacedKey;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.world.*;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockEntityType;
import org.machinemc.api.world.blocks.BlockHandler;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.api.world.dimensions.DimensionType;
import org.machinemc.api.world.generation.GeneratedSection;
import org.machinemc.api.world.generation.Generator;
import org.machinemc.landscape.Landscape;
import org.machinemc.landscape.Segment;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.server.chunk.ChunkSection;
import org.machinemc.server.chunk.ChunkUtils;
import org.machinemc.server.utils.FileUtils;
import org.machinemc.server.utils.WeaklyTimedCache;
import org.machinemc.server.world.blocks.WorldBlockManager;
import org.machinemc.server.world.generation.StonePyramidGenerator;
import org.machinemc.server.world.region.DefaultLandscapeHandler;
import org.machinemc.server.world.region.LandscapeChunk;
import org.machinemc.server.world.region.LandscapeHelper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import static org.machinemc.server.chunk.ChunkUtils.getSectionRelativeCoordinate;

/**
 * Server with a folder in the main server directory.
 */
public class ServerWorld extends AbstractWorld {

    public static final String DEFAULT_WORLD_FOLDER = "level";

    @Getter
    private final File folder;
    private final File regionFolder;

    protected final Set<Entity> entityList = new CopyOnWriteArraySet<>();

    private final Generator generator = new StonePyramidGenerator(getServer(), getSeed());

    private final LandscapeHelper landscapeHelper;
    @Getter
    private final WorldBlockManager worldBlockManager;

    // TODO configurable delay
    private final Cache<Long, LandscapeChunk> cachedChunks = new WeaklyTimedCache<>(16, TimeUnit.SECONDS);

    /**
     * Creates default server world.
     * @param server server
     * @return default server world
     */
    public static World createDefault(final Machine server) {
        Objects.requireNonNull(server, "Server can not be null");
        final File directory = new File(server.getDirectory(), DEFAULT_WORLD_FOLDER + "/");
        if (!directory.exists() && !directory.mkdirs())
            throw new RuntimeException("Failed to create the world directory " + directory.getPath());
        final World world = new ServerWorld(
                directory,
                server,
                NamespacedKey.machine("main"),
                server.getDimensionTypeManager().getDimensions().iterator().next(),
                server.getProperties().getDefaultWorldType(),
                1);
        world.setWorldSpawn(new Location(0, world.getDimensionType().getMinY(), 0, world));
        world.setDifficulty(server.getProperties().getDefaultDifficulty());
        return world;
    }

    public ServerWorld(final File folder,
                       final Server server,
                       final NamespacedKey name,
                       final DimensionType dimensionType,
                       final WorldType worldType,
                       final long seed) {
        super(server, name, FileUtils.getOrCreateUUID(folder), dimensionType, worldType, seed);
        this.folder = Objects.requireNonNull(folder, "World directory can not be null");
        regionFolder = new File(folder.getPath() + "/region/");
        landscapeHelper = new LandscapeHelper(this,
                regionFolder,
                new DefaultLandscapeHandler(
                        server.getBlockManager(),
                        server.getBiomeManager(),
                        false,  // TODO auto save should be configurable
                        256)  // TODO auto save limit should be configurable
        );
        worldBlockManager = new WorldBlockManager(this, position -> {
            getChunk(position); // loads the chunk in case it's not generated yet
            final Segment segment = getSegment(position);
            return server.getBlockType(LazyNamespacedKey.lazy(segment.getBlock(
                    getSectionRelativeCoordinate(position.getX()),
                    getSectionRelativeCoordinate(position.getY() - getDimensionType().getMinY()),
                    getSectionRelativeCoordinate(position.getZ())
            )))
                    .or(() ->
                            server.getBlockType(LazyNamespacedKey.lazy(landscapeHelper.getHandler().getDefaultType())))
                    .orElseThrow(() -> new NullPointerException("Provided default block type "
                            + landscapeHelper.getHandler().getDefaultType()
                            + " is not registered in the server block manager"));
        });
    }

    /**
     * Returns a segment at given position.
     * @param position position
     * @return segment at given position
     */
    private Segment getSegment(final BlockPosition position) {
        try {
            final Landscape landscape = landscapeHelper.get(position.getX(), position.getZ());
            final int segmentX = ChunkUtils.getChunkCoordinate(position.getX()) & 0xF;
            final int segmentZ = ChunkUtils.getChunkCoordinate(position.getZ()) & 0xF;
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
        if (loaded) throw new UnsupportedOperationException("The world has already been loaded");
        if (!regionFolder.mkdirs() && !regionFolder.exists())
            throw new IllegalStateException("Could not create the region folder for the world");
        loaded = true;
        getServer().getConsole().info("Loaded world '" + getName() + "'");
    }

    @Override
    @Synchronized
    public void unload() throws IOException {
        if (!loaded) throw new UnsupportedOperationException("The world has not been loaded yet");
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

    /**
     * Loads the player to the world.
     * @param player player to load
     */
    public void loadPlayer(final Player player) {
        Objects.requireNonNull(player, "Player to load can not be null");
        // TODO this should take player's view distance
        final Scheduler scheduler = getServer().getScheduler();
        final int chunksPerTask = 10;
        final int tasks = 3;
        for (int i = 0; i <= tasks; i++) {
            final int index = i;
            Scheduler.task((input, session) -> {
                final int start = index * chunksPerTask;
                final int end = (index + 1) * chunksPerTask;
                for (int j = start; j < end; j++) {
                    final int[] coordinates = getSpiralCoordinates(j);
                    final Chunk chunk = getChunk(coordinates[0], coordinates[1]);
                    chunk.sendChunk(player);
                }
                return null;
            }).async().run(scheduler);
        }
    }

    /**
     * Returns a x;y coordinates of a spiral.
     * @param orderIndex index
     * @return x and y coordinates in the spiral
     */
    private static int[] getSpiralCoordinates(final int orderIndex) {
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

        return new int[] {x, y};
    }

    /**
     * Unloads the player from the world.
     * @param player player to unload
     */
    public void unloadPlayer(final Player player) {
        // TODO implement player unloading
    }

    @Override
    public boolean spawn(final Entity entity) {
        Objects.requireNonNull(entity, "Entity to spawn can not be null");
        if (entity.getWorld() != this) return false;
        if (entity instanceof Player player)
            loadPlayer(player);
        return entityList.add(entity); // TODO implement entity spawning
    }

    @Override
    public boolean remove(final Entity entity) {
        Objects.requireNonNull(entity, "Entity to remove can not be null");
        if (!entityList.contains(entity)) return false;
        if (entity instanceof Player player)
            unloadPlayer(player);
        return entityList.remove(entity); // TODO implement entity removing
    }

    @Override
    @Synchronized
    public Chunk getChunk(final int chunkX, final int chunkZ) {
        try {
            final long chunkIndex = chunkIndex(chunkX, chunkZ);
            final LandscapeChunk chunk = cachedChunks.get(chunkIndex,
                    () -> new LandscapeChunk(this, worldBlockManager, chunkX, chunkZ, landscapeHelper)
            );

            for (int i = 0; i <= chunk.getMaxSection(); i++) {
                final int sectionIndex = i;
                final int ry = getDimensionType().getMinY() + Chunk.CHUNK_SECTION_SIZE * i;
                final Segment segment = chunk.getSegment(i);
                if (!segment.isEmpty()) continue; // if the segment has been generated before, skip

                final GeneratedSection content = generator.populateChunk(chunkX, chunkZ, i, this);

                final BlockType[] blockPalette = content.getBlockPalette();
                final Biome[] biomePalette = content.getBiomePalette();

                assert blockPalette.length != 0 && biomePalette.length != 0;

                final short[] blocksData = content.getBlockData();
                final short[] biomesData = content.getBiomeData();

                final NBTCompound[] tileEntities = content.getTileEntitiesData();

                // Section is created as well; generated chunks are expected
                // to be sent to client, if yes the intermediate step
                // of conversion between Landscape segment and section is
                // skipped which makes the process of loading newly generated
                // chunks much faster.
                final Section section = new ChunkSection(chunk, i,  () -> {
                    segment.push(); // if compound is requested we push the segment in case it's changed later
                    return segment.getDataCompound();
                });

                // There are multiple block types in the generated section
                if (blockPalette.length != 1) {
                    segment.setAllBlocks((x, y, z) -> {
                        final int blockIndex = Section.index(x, y, z);
                        final BlockType blockType = blockPalette[blocksData[blockIndex]];
                        final BlockPosition position = new BlockPosition(
                                Chunk.CHUNK_SIZE_X * chunkX + x,
                                ry + y,
                                Chunk.CHUNK_SIZE_Z * chunkZ + z);

                        // Initialization of block entities
                        if (blockType instanceof BlockEntityType blockEntityType) {
                            segment.setNBT(x, y, z, initializeTileEntity(
                                    blockEntityType,
                                    position,
                                    tileEntities[blockIndex]));
                        }

                        // Getting correct visual for the section's block palette
                        BlockData visual;
                        if (blockType.hasDynamicVisual()) {
                            final WorldBlock.State state = new WorldBlock.State(
                                    this,
                                    position,
                                    blockType,
                                    segment.getNBT(x, y, z).clone());
                            visual = blockType.getBlockData(state);
                            for (final BlockHandler blockHandler : blockType.getHandlers())
                                visual = blockHandler.onVisualRequest(state, visual);
                        } else {
                            visual = blockType.getBlockData(null);
                        }
                        section.getBlockPalette().set(x, y, z, visual.getID());

                        // Setting client visible nbt data for the section
                        if (blockType instanceof BlockEntityType blockEntityType && blockEntityType.sendsToClient()) {
                            final WorldBlock.State state = new WorldBlock.State(
                                    this,
                                    position,
                                    blockEntityType,
                                    segment.getNBT(x, y, z).clone());
                            section.getClientBlockEntities().put(Section.index(x, y, z),
                                    new Section.BlockEntity(
                                            (byte) x,
                                            (short) (y + sectionIndex * Chunk.CHUNK_SECTION_SIZE
                                                    + getDimensionType().getMinY()),
                                            (byte) z,
                                            blockEntityType.getBlockEntityBase(state)
                                                    .orElseThrow(NullPointerException::new),
                                            blockEntityType.getClientVisibleNBT(state)
                                                    .orElseThrow(NullPointerException::new)
                                    ));
                        }

                        return blockType.getName().toString();
                    });

                // There is only a single block type in the whole generated section
                } else {
                    final BlockType blockType = blockPalette[0];
                    segment.fill(blockType.getName().toString()); // we can fill the segment

                    // If the block type is block entity we need to initialize
                    // each block in the section, plus in this part
                    // we can set the client visible nbt as well
                    if (blockType instanceof BlockEntityType blockEntityType) {
                        segment.setAllNBT((x, y, z) -> {
                            final BlockPosition position = new BlockPosition(
                                    Chunk.CHUNK_SIZE_X * chunkX + x,
                                    ry + y,
                                    Chunk.CHUNK_SIZE_Z * chunkZ + z);
                            final NBTCompound compound = initializeTileEntity(blockEntityType, position,
                                    tileEntities[Section.index(x, y, z)]);

                            if (blockEntityType.sendsToClient()) {
                                final WorldBlock.State state = new WorldBlock.State(
                                        this,
                                        position,
                                        blockEntityType,
                                        compound);
                                section.getClientBlockEntities().put(Section.index(x, y, z),
                                        new Section.BlockEntity(
                                                (byte) x,
                                                (short) (y + sectionIndex * Chunk.CHUNK_SECTION_SIZE
                                                        + getDimensionType().getMinY()),
                                                (byte) z,
                                                blockEntityType.getBlockEntityBase(state)
                                                        .orElseThrow(NullPointerException::new),
                                                blockEntityType.getClientVisibleNBT(state)
                                                        .orElseThrow(NullPointerException::new)));
                            }

                            return compound;
                        });
                    }

                    // If the block type has dynamic visual,
                    // each block needs to be handled separately
                    if (blockType.hasDynamicVisual()) {
                        segment.getAllNBT((x, y, z, nbt) -> {
                            final WorldBlock.State state = new WorldBlock.State(this,
                                    new BlockPosition(Chunk.CHUNK_SIZE_X * chunkX + x,
                                            ry + y,
                                            Chunk.CHUNK_SIZE_Z * chunkZ + z),
                                    blockType,
                                    segment.getNBT(x, y, z).clone());
                            BlockData visual = blockType.getBlockData(state);
                            for (final BlockHandler blockHandler : blockType.getHandlers())
                                visual = blockHandler.onVisualRequest(state, visual);
                            section.getBlockPalette().set(x, y, z, visual.getID()
                            );
                        });
                    } else {
                        section.getBlockPalette().fill(blockType.getBlockData(null).getID());
                    }

                }

                // Biome generation
                if (biomePalette.length != 1) {
                    final Map<Biome, Integer> idMap = new HashMap<>();
                    for (final Biome biome : biomePalette)
                        idMap.put(biome, getServer().getBiomeManager().getBiomeID(biome));
                    segment.setAllBiomes((x, y, z) -> {
                        final Biome biome = biomePalette[biomesData[Section.index(x, y, z)]];
                        section.getBlockPalette().set(x, y, z, idMap.get(biome));
                        return biome.getName().toString();
                    });
                } else {
                    segment.fillBiome(biomePalette[0].getName().toString());
                    section.getBiomePalette().fill(getServer().getBiomeManager().getBiomeID(biomePalette[0]));
                }

                chunk.setSection(i, section); // we set the section manually
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
    private NBTCompound initializeTileEntity(final BlockEntityType entityBlockType,
                                             final BlockPosition position,
                                             final @Nullable NBTCompound generatorData) {
        final WorldBlock.State state = new WorldBlock.State(this, position, entityBlockType, new NBTCompound());
        entityBlockType.initialize(state);
        for (final BlockHandler handler : entityBlockType.getHandlers())
            handler.onGeneration(state);
        if (generatorData != null)
            generatorData.clone().forEach(state.compound()::set);
        return state.compound();
    }

    /**
     * @param chunkX x coordinate of the chunk
     * @param chunkZ z coordinate of the chunk
     * @return unique index for a chunk at given coordinates
     */
    private long chunkIndex(final int chunkX, final int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

}
