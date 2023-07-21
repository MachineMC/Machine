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
package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.chunk.data.ChunkData;
import org.machinemc.server.chunk.data.LightData;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.api.utils.FriendlyByteBuf;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PacketPlayOutChunkData extends PacketOut {

    private static final int ID = 0x21;

    private int chunkX;
    private int chunkZ;
    private ChunkData chunkData;
    private LightData lightData;

    static {
        register(PacketPlayOutChunkData.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChunkData::new);
    }

    public PacketPlayOutChunkData(final ServerBuffer buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
        chunkData = new ChunkData(buf);
        lightData = new LightData(buf);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeInt(chunkX)
                .writeInt(chunkZ)
                .write(chunkData)
                .write(lightData)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChunkData(new FriendlyByteBuf(serialize()));
    }

}
