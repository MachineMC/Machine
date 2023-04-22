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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.world.region;

import com.google.common.cache.Cache;
import io.netty.util.internal.UnstableApi;
import lombok.Synchronized;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.chunk.Section;
import org.machinemc.api.chunk.palette.Palette;
import org.machinemc.api.utils.LazyNamespacedKey;
import org.machinemc.api.world.BlockData;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.biomes.BiomeManager;
import org.machinemc.api.world.blocks.*;
import org.machinemc.landscape.Landscape;
import org.machinemc.landscape.Segment;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.chunk.SectionImpl;
import org.machinemc.server.chunk.WorldChunk;
import org.machinemc.server.utils.WeaklyTimedCache;
import org.machinemc.server.world.blocks.WorldBlockManager;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.machinemc.server.chunk.ChunkUtils.getSectionRelativeCoordinate;

/**
 * Chunk implementation using the Landscape world format.
 */
@SuppressWarnings("UnstableApiUsage")
public class LandscapeChunk extends WorldChunk {

    private final int worldX = chunkX * 16, worldZ = chunkZ * 16;
    private final Landscape landscape;
    private final WorldBlockManager worldBlockManager;
    private final int segmentX;
    private final int segmentZ;

    private final Cache<Integer, Section> sections = new WeaklyTimedCache<>(16, TimeUnit.SECONDS); // TODO configurable

    public LandscapeChunk(final World world,
                          final WorldBlockManager worldBlockManager,
                          final int chunkX,
                          final int chunkZ,
                          final LandscapeHelper helper) throws ExecutionException {
        super(world, chunkX, chunkZ);
        landscape = helper.get(chunkX * 16, chunkZ * 16);
        this.worldBlockManager = worldBlockManager;
        segmentX = getSectionRelativeCoordinate(chunkX);
        segmentZ = getSectionRelativeCoordinate(chunkZ);
    }

    /**
     * Returns a segment of this chunk at given index (y level index).
     * @param index index of the segment
     * @return segment with given index of this chunk
     */
    public Segment getSegment(final int index) {
        return landscape.loadSegment(segmentX, index, segmentZ);
    }

    @Override
    @Synchronized
    public WorldBlock getBlock(final int x, final int y, final int z) {
        checkCoordinates(x, y, z);
        // world block instances are managed by the world block managers
        return worldBlockManager.get(new BlockPosition(worldX + x, y, worldZ + z));
    }

    @Override
    @Synchronized
    public void setBlock(final int x, final int y, final int z, final BlockType blockType) {
        checkCoordinates(x, y, z);
        final int offsetY = y - getBottom();
        final int sectionY = getSectionRelativeCoordinate(offsetY);
        final int sectionIndex = offsetY / Chunk.CHUNK_SECTION_SIZE;

        final Segment segment = getSegment(sectionIndex);

        segment.setBlock(x, sectionY, z, blockType.getName().toString());

        // Creates NBT data for the block entities
        if (blockType instanceof BlockEntityType blockEntityType) {
            final WorldBlock.State state = new WorldBlock.State(
                    world,
                    new BlockPosition(x, y, z),
                    blockEntityType, new NBTCompound()
            );
            blockEntityType.initialize(state);
            for (final BlockHandler handler : blockType.getHandlers())
                handler.onPlace(state);
            segment.setNBT(x, sectionY, z, state.compound());
        } else {
            segment.setNBT(x, sectionY, z, null);
        }

        final Section section = sections.getIfPresent(sectionIndex);
        // updates the visual and client nbt if the section is present
        if (section != null)
            setSectionBlock(section, sectionIndex, x, sectionY, z, blockType);

        segment.push();
    }

    @Override
    @Synchronized
    public NBTCompound getBlockNBT(final int x, final int y, final int z) {
        checkCoordinates(x, y, z);
        final int offsetY = y - getBottom();
        final int sectionY = getSectionRelativeCoordinate(offsetY);
        final int sectionIndex = offsetY / Chunk.CHUNK_SECTION_SIZE;
        final Segment segment = getSegment(sectionIndex);
        return segment.getNBT(x, sectionY, z).clone();
    }

    @Override
    @Synchronized
    public void mergeBlockNBT(final int x, final int y, final int z, final NBTCompound compound) {
        checkCoordinates(x, y, z);
        final int offsetY = y - getBottom();
        final int sectionY = getSectionRelativeCoordinate(offsetY);
        final int sectionIndex = offsetY / Chunk.CHUNK_SECTION_SIZE;
        final Segment segment = getSegment(sectionIndex);
        final NBTCompound original = segment.getNBT(x, sectionY, z);
        original.putAll(compound.clone());

        final Section section = sections.getIfPresent(sectionIndex);
        // updates the visual and client nbt if the section is present
        if (section != null) {
            BlockType blockType = server.getBlockType(LazyNamespacedKey.lazy(segment.getBlock(x, sectionY, z)));
            if (blockType == null) {
                blockType = server.getBlockType(
                        LazyNamespacedKey.lazy(segment.getSource().getHandler().getDefaultType())
                );
                if (blockType == null) throw new IllegalStateException();
            }
            setSectionBlock(section, sectionIndex, x, sectionY, z, blockType);
        }

        segment.push();
    }

    @Override
    @Synchronized
    public void setBlockNBT(final int x, final int y, final int z, final @Nullable NBTCompound compound) {
        checkCoordinates(x, y, z);
        final int offsetY = y - getBottom();
        final int sectionY = getSectionRelativeCoordinate(offsetY);
        final int sectionIndex = offsetY / Chunk.CHUNK_SECTION_SIZE;
        final Segment segment = getSegment(sectionIndex);
        segment.setNBT(x, sectionY, z, compound != null ? compound.clone() : null);

        final Section section = sections.getIfPresent(sectionIndex);
        // updates the visual and client nbt if the section is present
        if (section != null) {
            BlockType blockType = server.getBlockType(LazyNamespacedKey.lazy(segment.getBlock(x, sectionY, z)));
            if (blockType == null) {
                blockType = server.getBlockType(
                        LazyNamespacedKey.lazy(segment.getSource().getHandler().getDefaultType())
                );
                if (blockType == null) throw new IllegalStateException();
            }
            setSectionBlock(section, sectionIndex, x, sectionY, z, blockType);
        }

        segment.push();
    }

    @Override
    @Synchronized
    public Biome getBiome(final int x, final int y, final int z) {
        checkCoordinates(x, y, z);
        final int offsetY = y - getBottom();
        final int sectionY = getSectionRelativeCoordinate(offsetY);
        final int sectionIndex = offsetY / Chunk.CHUNK_SECTION_SIZE;

        final Segment segment = getSegment(sectionIndex);

        Biome biome = world.getServer().getBiome(LazyNamespacedKey.lazy(segment.getBiome(x, sectionY, z)));
        if (biome != null) return biome;
        biome = world.getServer().getBiome(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultBiome()));
        if (biome == null) throw new IllegalStateException();
        setBiome(x, offsetY, z, biome);
        return biome;
    }

    @Override
    @Synchronized
    public void setBiome(final int x, final int y, final int z, final Biome biome) {
        checkCoordinates(x, y, z);
        final int offsetY = y - getBottom();
        final int sectionY = getSectionRelativeCoordinate(offsetY);
        final int sectionIndex = offsetY / Chunk.CHUNK_SECTION_SIZE;

        final Segment segment = getSegment(sectionIndex);

        segment.setBiome(x, sectionY, z, biome.getName().toString());

        final Section section = sections.getIfPresent(sectionIndex);
        // updates the section biome palette if the section is present
        if (section != null)
            section.getBiomePalette().set(
                    x / 4,
                    sectionY / 4,
                    z / 4,
                    getServer().getBiomeManager().getBiomeId(biome)
            ); // biome palette's dimension is 4 (xyz/4)

        segment.push();
    }

    /**
     * Checks whether the provided coordinates are within the chunk's area.
     * @param x x
     * @param y y
     * @param z z
     */
    private void checkCoordinates(final int x, final int y, final int z) {
        if (y > getTop()) throw new IllegalStateException("Maximum height of the world exceeded");
        if (x > 15 || x < 0) throw new IllegalStateException("The x coordinate is outside the chunk area");
        if (z > 15 || z < 0) throw new IllegalStateException("The x coordinate is outside the chunk area");
    }

    @Override
    public @Unmodifiable List<Section> getSections() {
        final List<Section> sections = new LinkedList<>();
        for (int i = getMinSection(); i <= getMaxSection(); i++)
            sections.add(getSection(i));
        return Collections.unmodifiableList(sections);
    }

    @Override
    @Synchronized
    public Section getSection(final int index) {
        if (index < getMinSection() || index > getMaxSection())
            throw new IndexOutOfBoundsException("Section with index " + index + " is outside "
                    + "the boundaries of this section");
        try {
            return sections.get(index, () -> {
                final Segment segment = getSegment(index);
                final SectionImpl section = new SectionImpl(this, index, () -> {
                    segment.push(); // if compound is requested we push the segment in case it's changed later
                    return segment.getDataCompound();
                });

                readSectionBlockData(section, index, segment);
                readSectionBiomeData(section, segment);

                return section;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Manually sets a section of given index in this chunk to a new one.
     * <p>
     * This is very unsafe if the provided section has incorrect data, make sure the provided
     * section is always processed the same way it would normally be.
     * <p>
     * Main purpose of this method is to speed up process of generation where the section
     * is created together with Landscape's generated segment,
     * {@link org.machinemc.server.world.ServerWorld#getChunk(int, int)}.
     * @param index index of the section to be set/replaced
     * @param section new section
     */
    @UnstableApi
    @Synchronized
    public void setSection(final int index, final Section section) {
        sections.put(index, section);
    }

    /**
     * Reads block data from a Landscape segment and writes those data into a section.
     * <p>
     * It calculates visuals of dynamic visual block types and creates client nbt for all
     * client nbt entity block types.
     * @param section section to write into
     * @param sectionIndex index of the section (required by handlers of block entities)
     * @param source segment source
     * @see #setSectionBlock(Section, int, int, int, int, BlockType)
     */
    public void readSectionBlockData(final Section section, final int sectionIndex, final Segment source) {

        // More than single block type is present in the source segment
        if (source.getBlockCount() != 1) {
            // lookup map to speed up conversion between String and BlockType
            final Map<String, BlockType> types = new HashMap<>();
            source.getAllBlocks((x, y, z, blockName) -> {
                if (types.containsKey(blockName)) {
                    final BlockType blockType = types.get(blockName);
                    // handles dynamic visuals and client nbt
                    setSectionBlock(section, sectionIndex, x, y, z, blockType);
                    return;
                }
                BlockType blockType = server.getBlockType(LazyNamespacedKey.lazy(blockName));
                if (blockType == null)
                    blockType = server.getBlockType(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultType()));
                assert blockType != null;
                types.put(blockName, blockType);
                setSectionBlock(section, sectionIndex, x, y, z, blockType); // handles dynamic visuals and client nbt
            });

        // Only a single block type is present in the source segment
        } else {
            BlockType blockType = server.getBlockType(LazyNamespacedKey.lazy(source.getBlockPalette().get(0)));
            if (blockType == null)
                blockType = server.getBlockType(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultType()));
            assert blockType != null;

            // Doesn't have dynamic visual, the palette only needs
            // to be filled with single value
            if (!blockType.hasDynamicVisual()) {
                section.getBlockPalette().fill(blockType.getBlockData(null).getId());

                // If block type is client nbt block entity we set client
                // nbt for each entry (no need to use #setSectionBlock,
                // we only need to set the client block entities)
                if (blockType instanceof BlockEntityType blockEntityType && blockEntityType.sendsToClient()) {
                    for (int x = 0; x < 16; x++)
                        for (int y = 0; y < 16; y++)
                            for (int z = 0; z < 16; z++)
                                setClientBlockEntity(section, sectionIndex, x, y, z, blockEntityType);
                }
                return;
            }

            // Has dynamic visual, we use #setSectionBlock to handle the rest
            for (int x = 0; x < 16; x++)
                for (int y = 0; y < 16; y++)
                    for (int z = 0; z < 16; z++)
                        setSectionBlock(section, sectionIndex, x, y, z, blockType);
        }
    }

    /**
     * Sets a block into a section, automatically handles dynamic visuals and client block entities.
     * @param section section
     * @param sectionIndex index of provided section
     * @param x x of the block in the section
     * @param y y of the block in the section
     * @param z z of the block in the section
     * @param blockType block type of the new block
     */
    public void setSectionBlock(final Section section, final int sectionIndex,
                                @Range(from = 0, to = 15) final int x,
                                @Range(from = 0, to = 15) final int y,
                                @Range(from = 0, to = 15) final int z,
                                final BlockType blockType) {
        BlockData visual;

        // Calculating dynamic visual
        if (blockType.hasDynamicVisual()) {
            final WorldBlock.State state = new WorldBlock.State(
                    world,
                    new BlockPosition(
                            worldX + x,
                            getBottom() + y + sectionIndex * Chunk.CHUNK_SECTION_SIZE,
                            worldZ + z),
                    blockType,
                    getSegment(sectionIndex).getNBT(x, y, z).clone());
            visual = blockType.getBlockData(state);
            for (final BlockHandler blockHandler : blockType.getHandlers())
                visual = blockHandler.onVisualRequest(state, visual);

        // Calculating not dynamic visual
        } else {
            visual = blockType.getBlockData(null);
        }


        section.getBlockPalette().set(x, y, z, visual.getId());

        // Sets client nbt in case the block supports it
        if (blockType instanceof BlockEntityType blockEntityType) {
            setClientBlockEntity(section, sectionIndex, x, y, z, blockEntityType);
        } else {
            section.getClientBlockEntities().remove(Section.index(x, y, z));
        }

    }

    /**
     * Creates the client block entity data for a block in a section.
     * @param section section
     * @param sectionIndex index of provided section
     * @param x x of the block in the section
     * @param y y of the block in the section
     * @param z z of the block in the section
     * @param blockEntityType block entity type of the new block
     */
    private void setClientBlockEntity(final Section section,
                                      final int sectionIndex,
                                      final int x,
                                      final int y,
                                      final int z,
                                      final BlockEntityType blockEntityType) {
        // If the block type doesn't support client nbt we remove it from client block entities
        if (!blockEntityType.sendsToClient()) {
            section.getClientBlockEntities().remove(Section.index(x, y, z));
            return;
        }

        final Map<Integer, Section.BlockEntity> blockEntityMap = section.getClientBlockEntities();
        final WorldBlock.State state = new WorldBlock.State(
                world,
                new BlockPosition(worldX + x, getBottom() + y + sectionIndex * Chunk.CHUNK_SECTION_SIZE, worldZ + z),
                blockEntityType,
                getSegment(sectionIndex).getNBT(x, y, z).clone());

        blockEntityMap.put(
                Section.index(x, y, z),
                new Section.BlockEntity(
                        (byte) x,
                        (short) (y + sectionIndex * Chunk.CHUNK_SECTION_SIZE + getBottom()),
                        (byte) z,
                        blockEntityType.getBlockEntityBase(state),
                        blockEntityType.getClientVisibleNBT(state))
        );
    }

    /**
     * Reads biome data from a Landscape segment and writes those data into a section.
     * @param section section to write into
     * @param source segment source
     */
    public void readSectionBiomeData(final Section section, final Segment source) {
        final Palette biomesPalette = section.getBiomePalette();
        if (source.getBiomesCount() != 1) {
            final Map<String, Integer> biomes = new HashMap<>();
            final BiomeManager biomeManager = server.getBiomeManager();
            source.getAllBiomes((x, y, z, biomeName) -> {
                if (x % 4 != 0 || y % 4 != 0 || z % 4 != 0) return; // biome palette's dimension is 4
                if (biomes.containsKey(biomeName)) {
                    biomesPalette.set(x / 4, y / 4, z / 4, biomes.get(biomeName));
                    return;
                }
                Biome biome = biomeManager.getBiome(LazyNamespacedKey.lazy(biomeName));
                if (biome == null)
                    biome = biomeManager.getBiome(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultBiome()));
                assert biome != null;
                final int id = biomeManager.getBiomeId(biome);
                biomes.put(biomeName, id);
                biomesPalette.set(x / 4, y / 4, z / 4, id);
            });
        } else {
            final BiomeManager biomeManager = server.getBiomeManager();
            Biome biome = biomeManager.getBiome(LazyNamespacedKey.lazy(source.getBiomePalette().get(0)));
            if (biome == null)
                biome = biomeManager.getBiome(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultBiome()));
            biomesPalette.fill(biomeManager.getBiomeId(biome));
        }
    }

    @Override
    public void reset() {
        for (int i = getMinSection(); i <= getMaxSection(); i++) {
            final Segment segment = getSegment(i);
            segment.reset();
            segment.push();
        }
        sections.invalidateAll();
    }

    @Override
    public Section.BlockEntity[] getClientBlockEntities() {
        final List<Section.BlockEntity> all = new ArrayList<>();
        for (final Section section : getSections())
            all.addAll(section.getClientBlockEntities().values());
        return all.toArray(new Section.BlockEntity[0]);
    }

}
