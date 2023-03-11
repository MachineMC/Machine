package org.machinemc.server.chunk;

import lombok.Getter;
import org.machinemc.api.chunk.Section;
import org.machinemc.api.entities.Player;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTLongArray;
import org.machinemc.server.Server;
import org.machinemc.api.chunk.Chunk;
import org.machinemc.api.world.World;
import org.machinemc.server.chunk.data.ChunkData;
import org.machinemc.server.chunk.data.LightData;
import org.machinemc.server.network.packets.out.play.PacketPlayOutChunkData;
import org.machinemc.server.network.packets.out.play.PacketPlayOutUnloadChunk;
import org.machinemc.server.network.packets.out.play.PacketPlayOutUpdateLight;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.server.utils.math.MathUtils;

import java.util.*;

/**
 * Default implementation of the chunk.
 */
@Getter
public abstract class WorldChunk implements Chunk {

    private final int maxSection, minSection;

    protected final Server server;
    protected final World world;

    protected final int chunkX, chunkZ;

    protected boolean loaded = true;

    private final int bottom;
    private final int height;

    public WorldChunk(final World world, final int chunkX, final int chunkZ) {
        server = world.getServer();
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        bottom = world.getDimensionType().getMinY();
        height = world.getDimensionType().getHeight();
        maxSection = height / 16 - 1;
        minSection = 0;
    }

    @Override
    public void sendChunk(final Player player) {
        player.sendPacket(createChunkPacket());
    }

    @Override
    public void unloadChunk(final Player player) {
        player.sendPacket(new PacketPlayOutUnloadChunk(chunkX, chunkZ));
    }

    /**
     * @return chunk data of this chunk
     */
    public ChunkData createChunkData() {
        final int[] motionBlocking = new int[16 * 16];
        final int[] worldSurface = new int[16 * 16];
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
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        for(final Section section : getSections())
            section.write(buf);
        final byte[] data = buf.bytes();

        return new ChunkData(heightmaps, data);
    }

    /**
     * @return light data of this chunk
     */
    public LightData createLightData() {
        final BitSet skyMask = new BitSet();
        final BitSet blockMask = new BitSet();
        final BitSet emptySkyMask = new BitSet();
        final BitSet emptyBlockMask = new BitSet();
        final List<byte[]> skyLights = new ArrayList<>();
        final List<byte[]> blockLights = new ArrayList<>();
        int index = 0;
        for (final Section section : getSections()) {
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
    public PacketPlayOutChunkData createChunkPacket() {
        return new PacketPlayOutChunkData(chunkX, chunkZ,
                createChunkData(),
                createLightData());
    }

    /**
     * @return light packet of this chunk
     */
    public PacketPlayOutUpdateLight createLightPacket() {
        return new PacketPlayOutUpdateLight(chunkX, chunkZ, createLightData());
    }

}
