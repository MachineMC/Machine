package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.chunk.data.ChunkData;
import org.machinemc.server.chunk.data.LightData;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutChunkData extends PacketOut {

    private static final int ID = 0x21;

    private int chunkX;
    private int chunkZ;
    private @NotNull ChunkData chunkData;
    private @NotNull LightData lightData;

    static {
        register(PacketPlayOutChunkData.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChunkData::new);
    }

    public PacketPlayOutChunkData(@NotNull ServerBuffer buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
        chunkData = new ChunkData(buf);
        lightData = new LightData(buf);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeInt(chunkX)
                .writeInt(chunkZ)
                .write(chunkData)
                .write(lightData)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutChunkData(new FriendlyByteBuf(serialize()));
    }

}
