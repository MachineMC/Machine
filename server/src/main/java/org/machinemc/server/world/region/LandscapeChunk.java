package org.machinemc.server.world.region;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.chunk.Section;
import org.machinemc.api.utils.LazyNamespacedKey;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.BlockVisual;
import org.machinemc.api.world.blocks.WorldBlock;
import org.machinemc.api.world.blocks.WorldBlockManager;
import org.machinemc.landscape.Landscape;
import org.machinemc.landscape.Segment;
import org.machinemc.server.chunk.SectionImpl;
import org.machinemc.server.chunk.WorldChunk;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SuppressWarnings("UnstableApiUsage")
public class LandscapeChunk extends WorldChunk {

    private final int worldX = chunkX * 16, worldZ = chunkZ * 16;
    private final Landscape landscape;
    private final WorldBlockManager worldBlockManager;
    private final int segmentX;
    private final int segmentZ;

    private final Cache<Integer, Section> sections = CacheBuilder.newBuilder()
            .softValues()
            .build();

    public LandscapeChunk(final World world, final int chunkX, final int chunkZ, final LandscapeHelper helper) throws ExecutionException {
        super(world, chunkX, chunkZ);
        landscape = helper.get(chunkX * 16, chunkZ * 16);
        worldBlockManager = world.getWorldBlockManager();
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
    public Future<WorldBlock> getBlockAsync(final int x, final int y, final int z) {
        return worldBlockManager.getAsync(new BlockPosition(worldX + x, y, worldZ + z));
    }

    @Override
    public void setBlock(final int x, final int y, final int z, final BlockType blockType) {
        final int offsetY = y - getBottom();
        final Segment segment = getSegment(offsetY / 16);
        segment.setBlock(x, offsetY % 16, z, blockType.getName().toString());

        if(blockType.isTileEntity())
            segment.setNBT(x, offsetY % 16, z, blockType.init(world, new BlockPosition(x, y, z)));

        segment.push();
    }

    @Override
    public Biome getBiome(final int x, final int y, final int z) {
        final int offsetY = y - getBottom();
        final Segment segment = getSegment(offsetY / 16);

        Biome biome = world.getServer().getBiomeManager().getBiome(LazyNamespacedKey.lazy(segment.getBiome(x, offsetY % 16, z)));
        if(biome != null) return biome;
        biome = world.getServer().getBiomeManager().getBiome(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultBiome()));
        if(biome == null) throw new IllegalStateException();
        setBiome(x, offsetY, z, biome);
        return biome;
    }

    @Override
    public void setBiome(final int x, final int y, final int z, final Biome biome) {
        final Segment segment = getSegment(y / 16);
        segment.setBiome(x, y % 16, z, biome.getName().toString());
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
                final Map<String, BlockType> types = new HashMap<>();

                segment.getAllBlocks((x, y, z, type) -> {
                    if (types.containsKey(type)) {
                        final BlockType blockType = types.get(type);
                        setSectionEntry(section, index, x, y, z, blockType);
                        return;
                    }
                    BlockType blockType = server.getBlockType(LazyNamespacedKey.lazy(type));
                    if (blockType == null)
                        blockType = server.getBlockType(LazyNamespacedKey.lazy(landscape.getHandler().getDefaultType()));
                    assert blockType != null;
                    types.put(blockType.getName().toString(), blockType);
                    setSectionEntry(section, index, x, y, z, blockType);
                });
                return section;
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void setSectionEntry(final Section section, final int sectionIndex, final int x, final int y, final int z, final BlockType blockType) {
        final BlockVisual visual;
        if(blockType.hasDynamicVisual()) {
            final WorldBlock block = getBlock(x, y + 16 * sectionIndex, z);
            visual = blockType.getVisual(block);
        } else {
            visual = blockType.getVisual(null);
        }
        section.getBlockPalette().set(x, y, z, visual.getBlockData().getId());
    }

    @Override
    public Chunk copy(World world, int chunkX, int chunkZ) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException(); // TODO
    }

}
