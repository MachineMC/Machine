package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.chunk.data.ChunkData;
import me.pesekjak.machine.chunk.data.LightData;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutChunkData extends PacketOut {

    private static final int ID = 0x21;

    private int chunkX;
    private int chunkZ;
    @NotNull
    private ChunkData chunkData;
    @NotNull
    private LightData lightData;

    static {
        register(PacketPlayOutChunkData.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChunkData::new);
    }

    public PacketPlayOutChunkData(FriendlyByteBuf buf) {
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
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeInt(chunkX)
                .writeInt(chunkZ);
        chunkData.write(buf);
        lightData.write(buf);
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChunkData(new FriendlyByteBuf(serialize()));
    }

}
