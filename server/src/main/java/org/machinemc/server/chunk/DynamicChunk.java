package org.machinemc.server.chunk;

import org.machinemc.api.chunk.Section;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTLongArray;
import org.machinemc.server.chunk.data.ChunkData;
import org.machinemc.server.chunk.data.LightData;
import org.machinemc.api.entities.Entity;
import org.machinemc.api.entities.Player;
import org.machinemc.server.network.packets.out.play.PacketPlayOutChunkData;
import org.machinemc.server.network.packets.out.play.PacketPlayOutUnloadChunk;
import org.machinemc.server.network.packets.out.play.PacketPlayOutUpdateLight;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.server.utils.math.MathUtils;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.World;
import org.machinemc.api.world.biomes.Biome;
import org.machinemc.server.world.blocks.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.world.blocks.BlockType;
import org.machinemc.api.world.blocks.BlockVisual;
import org.machinemc.api.world.blocks.WorldBlock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Chunk that can change individual blocks and biomes at any time.
 */
public class DynamicChunk extends WorldChunk {

    private final Map<Integer, WorldBlock> blocks = new ConcurrentHashMap<>();
    private final List<SectionImpl> sections = new ArrayList<>();

    private final int bottom;
    private final int height;

    public DynamicChunk(@NotNull World world, int chunkX, int chunkZ) {
        super(world, chunkX, chunkZ);
        if(world.getManager() == null) throw new IllegalStateException("The world has to have a manager");
        bottom = world.getDimensionType().getMinY();
        height = world.getDimensionType().getHeight();
        for(int i = 0; i < height / 16; i++)
            sections.add(new SectionImpl());
    }

    @Override
    public @NotNull WorldBlock getBlock(int x, int y, int z) {
        return blocks.get(ChunkUtils.getBlockIndex(x, y, z));
    }

    @Override
    public @NotNull WorldBlock setBlock(int x, int y, int z, @NotNull BlockType blockType, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source) {
        final WorldBlock previous = getBlock(x, y, z);
        if(previous != null) // TODO this can happen but shouldn't, fix
            previous.getBlockType().destroy(previous, replaceReason != null ? replaceReason : BlockTypeImpl.DestroyReason.OTHER, null);
        final int index = ChunkUtils.getBlockIndex(x, y, z);
        BlockPosition position = ChunkUtils.getBlockPosition(index, chunkX, chunkZ);
        position = position.withY(position.getY() + bottom); // offset from bottom
        WorldBlockImpl block = new WorldBlockImpl(blockType, position, world);
        block.getBlockType().create(block, reason != null ? reason : BlockTypeImpl.CreateReason.OTHER, source);
        setVisual(x, y, z, block.getVisual());
        blocks.put(index, block);
        return block;
    }

    @Override
    public void setVisual(int x, int y, int z, @NotNull BlockVisual visual) {
        getSectionAt(y).getBlockPalette().set(
                ChunkUtils.getSectionRelativeCoordinate(x),
                ChunkUtils.getSectionRelativeCoordinate(y),
                ChunkUtils.getSectionRelativeCoordinate(z),
                visual.getBlockData().getId());
    }

    @Override
    public @NotNull Biome getBiome(int x, int y, int z) {
        final Section section = getSectionAt(y);
        final int id = section.getBiomePalette().get(
                ChunkUtils.getSectionRelativeCoordinate(x) / 4,
                ChunkUtils.getSectionRelativeCoordinate(y) / 4,
                ChunkUtils.getSectionRelativeCoordinate(z) / 4);
        if(world.getManager() == null) throw new IllegalStateException();
        final Biome biome = world.getManager().getServer().getBiomeManager().getById(id);
        if(biome == null) throw new IllegalStateException();
        return biome;
    }

    @Override
    public void setBiome(int x, int y, int z, @NotNull Biome biome) {
        final Section section = getSectionAt(y);
        section.getBiomePalette().set(x / 4, y / 4, z / 4, biome.getId());
    }

    @Override
    public @NotNull List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public @NotNull Section getSection(int section) {
        if(sections.get(section) == null) throw new IllegalStateException();
        return sections.get(section);
    }

    @Override
    public void sendChunk(@NotNull Player player) {
        player.sendPacket(createChunkPacket());
    }

    @Override
    public void unloadChunk(@NotNull Player player) {
        player.sendPacket(new PacketPlayOutUnloadChunk(chunkX, chunkZ));
    }

    @Override
    public @NotNull WorldChunk copy(@NotNull World world, int chunkX, int chunkZ) {
        DynamicChunk copy = new DynamicChunk(world, chunkX, chunkZ);
        for(int i : blocks.keySet())
            copy.blocks.put(i, blocks.get(i));
        for(int i = 0; i < sections.size(); i++)
            copy.sections.set(i, sections.get(i));
        return copy;
    }

    @Override
    public void reset() {
        blocks.clear();
        sections.clear();
        for(int i = 0; i < height / 16; i++)
            sections.add(new SectionImpl());
    }

    /**
     * @return chunk data of this chunk
     */
    private @NotNull ChunkData createChunkData() {
        int[] motionBlocking = new int[16 * 16];
        int[] worldSurface = new int[16 * 16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                motionBlocking[x + z * 16] = 0;
                worldSurface[x + z * 16] = height - 1;
            }
        }

        final int bitsForHeight = MathUtils.bitsToRepresent(height);
        final NBTCompound heightmaps = new NBTCompound(Map.of(
                "MOTION_BLOCKING", new NBTLongArray(ChunkUtils.encodeBlocks(motionBlocking, bitsForHeight)),
                "WORLD_SURFACE", new NBTLongArray(ChunkUtils.encodeBlocks(worldSurface, bitsForHeight))));

        // Data
        FriendlyByteBuf buf = new FriendlyByteBuf();
        for(SectionImpl section : sections) section.write(buf);
        final byte[] data = buf.bytes();

        return new ChunkData(heightmaps, data);
    }

    /**
     * @return light data of this chunk
     */
    private @NotNull LightData createLightData() {
        BitSet skyMask = new BitSet();
        BitSet blockMask = new BitSet();
        BitSet emptySkyMask = new BitSet();
        BitSet emptyBlockMask = new BitSet();
        List<byte[]> skyLights = new ArrayList<>();
        List<byte[]> blockLights = new ArrayList<>();
        int index = 0;
        for (SectionImpl section : sections) {
            index++;
            final byte[] skyLight = section.getSkyLight();
            final byte[] blockLight = section.getBlockLight();
            if (skyLight.length != 0) {
                skyLights.add(skyLight);
                skyMask.set(index);
            } else {
                emptySkyMask.set(index);
            }
            if (blockLight.length != 0) {
                blockLights.add(blockLight);
                blockMask.set(index);
            } else {
                emptyBlockMask.set(index);
            }
        }
        return new LightData(true,
                skyMask, blockMask,
                emptySkyMask, emptyBlockMask,
                skyLights, blockLights);
    }

    /**
     * @return chunk packet of this chunk
     */
    private @NotNull PacketPlayOutChunkData createChunkPacket() {
        return new PacketPlayOutChunkData(chunkX, chunkZ,
                createChunkData(),
                createLightData());
    }

    /**
     * @return light packet of this chunk
     */
    private @NotNull PacketPlayOutUpdateLight createLightPacket() {
        return new PacketPlayOutUpdateLight(chunkX, chunkZ, createLightData());
    }

}
