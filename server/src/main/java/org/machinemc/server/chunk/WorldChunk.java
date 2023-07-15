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
    private final int top;
    private final int height;

    public WorldChunk(final World world, final int chunkX, final int chunkZ) {
        server = world.getServer();
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        bottom = world.getDimensionType().getMinY();
        top = world.getDimensionType().getMaxY();
        height = world.getDimensionType().getHeight();
        maxSection = height / Chunk.CHUNK_SECTION_SIZE - 1;
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
     * @param sections sections for the chunk data
     * @param blockEntities block entities in the chunk
     * @param height height of the chunk
     * @return chunk data of this chunk
     */
    public static ChunkData createChunkData(final List<Section> sections,
                                            final Section.BlockEntity[] blockEntities,
                                            final int height) {
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
        for (final Section section : sections)
            section.write(buf);
        final byte[] data = buf.bytes();

        return new ChunkData(heightmaps, data, blockEntities);
    }

    /**
     * @param sections for the light data
     * @return light data of this chunk
     */
    public static LightData createLightData(final List<Section> sections) {
        final BitSet skyMask = new BitSet();
        final BitSet blockMask = new BitSet();
        final BitSet emptySkyMask = new BitSet();
        final BitSet emptyBlockMask = new BitSet();
        final List<byte[]> skyLights = new ArrayList<>();
        final List<byte[]> blockLights = new ArrayList<>();
        int index = 0;
        for (final Section section : sections) {
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
        return new LightData(skyMask, blockMask,
                emptySkyMask, emptyBlockMask,
                skyLights, blockLights);
    }

    /**
     * Returns all client block entities present in the chunk, order
     * of the array doesn't matter.
     * @return block entities of this chunk
     */
    public abstract Section.BlockEntity[] getClientBlockEntities();

    /**
     * @return chunk packet of this chunk
     */
    public PacketPlayOutChunkData createChunkPacket() {
        final List<Section> sections = getSections();
        return new PacketPlayOutChunkData(chunkX, chunkZ,
                createChunkData(sections, getClientBlockEntities(), height),
                createLightData(sections));
    }

    /**
     * @return light packet of this chunk
     */
    public PacketPlayOutUpdateLight createLightPacket() {
        return new PacketPlayOutUpdateLight(chunkX, chunkZ, createLightData(getSections()));
    }

}
