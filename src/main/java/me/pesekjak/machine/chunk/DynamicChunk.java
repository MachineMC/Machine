package me.pesekjak.machine.chunk;

import me.pesekjak.machine.chunk.data.ChunkData;
import me.pesekjak.machine.chunk.data.LightData;
import me.pesekjak.machine.entities.Entity;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.network.packets.out.PacketPlayOutChunkData;
import me.pesekjak.machine.network.packets.out.PacketPlayOutUnloadChunk;
import me.pesekjak.machine.network.packets.out.PacketPlayOutUpdateLight;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.math.MathUtils;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.World;
import me.pesekjak.machine.world.biomes.Biome;
import me.pesekjak.machine.world.blocks.BlockType;
import me.pesekjak.machine.world.blocks.BlockVisual;
import me.pesekjak.machine.world.blocks.WorldBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Chunk that can change individual blocks and biomes at any time.
 */
public class DynamicChunk extends Chunk {

    private final Map<Integer, WorldBlock> blocks = new ConcurrentHashMap<>();
    private final List<Section> sections = new ArrayList<>();

    private final int bottom;
    private final int height;

    public DynamicChunk(World world, int chunkX, int chunkZ) {
        super(world, chunkX, chunkZ);
        if(world.manager() == null) throw new IllegalStateException("The world has to have a manager");
        bottom = world.getDimensionType().getMinY();
        height = world.getDimensionType().getHeight();
        for(int i = 0; i < height / 16; i++)
            sections.add(new Section());
    }

    @Override
    public WorldBlock getBlock(int x, int y, int z) {
        return blocks.get(ChunkUtils.getBlockIndex(x, y, z));
    }

    @Override
    public WorldBlock setBlock(int x, int y, int z, @NotNull BlockType blockType, @Nullable BlockType.CreateReason reason, @Nullable BlockType.DestroyReason replaceReason, @Nullable Entity source) {
        final WorldBlock previous = getBlock(x, y, z);
        if(previous != null)
            previous.getBlockType().destroy(previous, replaceReason != null ? replaceReason : BlockType.DestroyReason.OTHER, null);
        final int index = ChunkUtils.getBlockIndex(x, y, z);
        BlockPosition position = ChunkUtils.getBlockPosition(index, chunkX, chunkZ);
        position.setY(position.getY() + bottom); // offset from bottom
        WorldBlock block = new WorldBlock(blockType, position, world);
        block.getBlockType().create(block, reason != null ? reason : BlockType.CreateReason.OTHER, source);
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
    public Biome getBiome(int x, int y, int z) {
        final Section section = getSectionAt(y);
        final int id = section.getBiomePalette().get(
                ChunkUtils.getSectionRelativeCoordinate(x) / 4,
                ChunkUtils.getSectionRelativeCoordinate(y) / 4,
                ChunkUtils.getSectionRelativeCoordinate(z) / 4);
        return world.manager().getServer().getBiomeManager().getById(id);
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
    public @NotNull Chunk copy(@NotNull World world, int chunkX, int chunkZ) {
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
            sections.add(new Section());
    }

    /**
     * @return chunk data of this chunk
     */
    private ChunkData createChunkData() {
        int[] motionBlocking = new int[16 * 16];
        int[] worldSurface = new int[16 * 16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                motionBlocking[x + z * 16] = 0;
                worldSurface[x + z * 16] = height - 1;
            }
        }

        final int bitsForHeight = MathUtils.bitsToRepresent(height);
        final NBTCompound heightmaps = NBT.Compound(Map.of(
                "MOTION_BLOCKING", NBT.LongArray(ChunkUtils.encodeBlocks(motionBlocking, bitsForHeight)),
                "WORLD_SURFACE", NBT.LongArray(ChunkUtils.encodeBlocks(worldSurface, bitsForHeight))));

        // Data
        FriendlyByteBuf buf = new FriendlyByteBuf();
        for(Section section : sections) section.write(buf);
        final byte[] data = buf.bytes();

        return new ChunkData(heightmaps, data);
    }

    /**
     * @return light data of this chunk
     */
    private LightData createLightData() {
        BitSet skyMask = new BitSet();
        BitSet blockMask = new BitSet();
        BitSet emptySkyMask = new BitSet();
        BitSet emptyBlockMask = new BitSet();
        List<byte[]> skyLights = new ArrayList<>();
        List<byte[]> blockLights = new ArrayList<>();
        int index = 0;
        for (Section section : sections) {
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
    private PacketPlayOutChunkData createChunkPacket() {
        return new PacketPlayOutChunkData(chunkX, chunkZ,
                createChunkData(),
                createLightData());
    }

    /**
     * @return light packet of this chunk
     */
    private PacketPlayOutUpdateLight createLightPacket() {
        return new PacketPlayOutUpdateLight(chunkX, chunkZ, createLightData());
    }

}
