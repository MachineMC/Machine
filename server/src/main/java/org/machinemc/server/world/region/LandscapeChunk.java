package org.machinemc.server.world.region;

import com.google.common.cache.Cache;
import org.jetbrains.annotations.Unmodifiable;
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

@SuppressWarnings("UnstableApiUsage")
public class LandscapeChunk extends WorldChunk {

    private final int worldX = chunkX * 16, worldZ = chunkZ * 16;
    private final Landscape landscape;
    private final WorldBlockManager worldBlockManager;
    private final int segmentX;
    private final int segmentZ;

    private final Cache<Integer, Section> sections = new WeaklyTimedCache<>(16, TimeUnit.SECONDS); // TODO configurable (check server world too, should be the same value)

    public LandscapeChunk(final World world, final WorldBlockManager worldBlockManager, final int chunkX, final int chunkZ, final LandscapeHelper helper) throws ExecutionException {
        super(world, chunkX, chunkZ);
        landscape = helper.get(chunkX * 16, chunkZ * 16);
        this.worldBlockManager = worldBlockManager;
        segmentX = Math.abs(chunkX) % 16;
        segmentZ = Math.abs(chunkZ) % 16;
    }

    public Segment getSegment(final int index) {
        return landscape.loadSegment(segmentX, index, segmentZ);
    }

    @Override
    public WorldBlock getBlock(final int x, final int y, final int z) {
        return worldBlockManager.get(new BlockPosition(worldX + x, y, worldZ + z));
    }

    @Override
    public void setBlock(final int x, final int y, final int z, final BlockType blockType) {
        final int offsetY = y - getBottom();
        final int sectionY = offsetY % 16;
        final int sectionIndex = offsetY / 16;

        final Segment segment = getSegment(sectionIndex);

        segment.setBlock(x, sectionY, z, blockType.getName().toString());

        if(blockType instanceof EntityBlockType entityBlockType) {
            final WorldBlock.State state = new WorldBlock.State(world, new BlockPosition(x, y, z), entityBlockType, new NBTCompound());
            entityBlockType.initialize(state);
            for(BlockHandler handler : blockType.getHandlers())
                handler.onPlace(state);
            segment.setNBT(x, sectionY, z, state.compound());
        } else {
            segment.setNBT(x, sectionY, z, null);
        }

        final Section section = sections.getIfPresent(sectionIndex);
        if(section != null)
            setSectionBlock(section, sectionIndex, x, sectionY, z, blockType);

        segment.push();
    }

    @Override
    public Biome getBiome(final int x, final int y, final int z) {
        final int offsetY = y - getBottom();
        final int sectionY = offsetY % 16;
        final int sectionIndex = offsetY / 16;

        final Segment segment = getSegment(sectionIndex);

        Biome biome = world.getServer().getBiome(LazyNamespacedKey.lazy(segment.getBiome(x, sectionY, z)));
        if(biome != null) return biome;
        biome = world.getServer().getBiome(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultBiome()));
        if(biome == null) throw new IllegalStateException();
        setBiome(x, offsetY, z, biome);
        return biome;
    }

    @Override
    public void setBiome(final int x, final int y, final int z, final Biome biome) {
        final int offsetY = y - getBottom();
        final int sectionY = offsetY % 16;
        final int sectionIndex = offsetY / 16;

        final Segment segment = getSegment(sectionIndex);

        segment.setBiome(x, y % 16, z, biome.getName().toString());

        final Section section = sections.getIfPresent(sectionIndex);
        if(section != null)
            section.getBiomePalette().set(x / 4, sectionY / 4, z / 4, getServer().getBiomeManager().getBiomeId(biome));

        segment.push();
    }

    @Override
    public @Unmodifiable List<Section> getSections() {
        final List<Section> sections = new LinkedList<>();
        for (int i = getMinSection(); i <= getMaxSection(); i++)
            sections.add(getSection(i));
        return Collections.unmodifiableList(sections);
    }

    @Override
    public Section getSection(final int index) {
        try {
            return sections.get(index, () -> {
                final SectionImpl section = new SectionImpl();
                final Segment segment = getSegment(index);

                readSectionBlockData(index, section, segment);
                readSectionBiomeData(section, segment);

                return section;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setSection(final int index, final Section section) {
        sections.put(index, section);
    }

    public void readSectionBlockData(final int sectionIndex, final Section section, final Segment source) {
        if(source.getBlockCount() != 1) {
            final Map<String, BlockType> types = new HashMap<>();
            source.getAllBlocks((x, y, z, blockName) -> {
                if (types.containsKey(blockName)) {
                    final BlockType blockType = types.get(blockName);
                    setSectionBlock(section, sectionIndex, x, y, z, blockType);
                    return;
                }
                BlockType blockType = server.getBlockType(LazyNamespacedKey.lazy(blockName));
                if (blockType == null)
                    blockType = server.getBlockType(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultType()));
                assert blockType != null;
                types.put(blockName, blockType);
                setSectionBlock(section, sectionIndex, x, y, z, blockType);
            });
        } else {
            BlockType blockType = server.getBlockType(LazyNamespacedKey.lazy(source.getBlockPalette().get(0)));
            if (blockType == null)
                blockType = server.getBlockType(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultType()));
            assert blockType != null;
            if(!blockType.hasDynamicVisual()) {
                section.getBlockPalette().fill(blockType.getBlockData(null).getId());
                return;
            }
            for (int x = 0; x < 16; x++)
                for (int y = 0; y < 16; y++)
                    for (int z = 0; z < 16; z++)
                        setSectionBlock(section, sectionIndex, x, y, z, blockType);
        }
    }

    private void setSectionBlock(final Section section, final int sectionIndex, final int x, final int y, final int z, final BlockType blockType) {
        BlockData visual;
        if(blockType.hasDynamicVisual()) {
            final WorldBlock.State state = new WorldBlock.State(
                    world,
                    new BlockPosition(worldX + x, getBottom() + y, worldZ + z),
                    blockType,
                    getSegment(sectionIndex).getNBT(x, y, z).clone());
            visual = blockType.getBlockData(state);
            for(final BlockHandler blockHandler : blockType.getHandlers())
                visual = blockHandler.onVisualRequest(state, visual);
        } else {
            visual = blockType.getBlockData(null);
        }
        section.getBlockPalette().set(x, y, z, visual.getId());
    }

    public void readSectionBiomeData(final Section section, final Segment source) {
        final Palette biomesPalette = section.getBiomePalette();
        if(source.getBiomesCount() != 1) {
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
    }

}
