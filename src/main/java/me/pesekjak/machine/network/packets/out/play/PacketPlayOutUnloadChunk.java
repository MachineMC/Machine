package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutUnloadChunk extends PacketOut {

    private static final int ID = 0x1C;

    @Getter @Setter
    private int chunkX, chunkZ;

    static {
        register(PacketPlayOutUnloadChunk.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUnloadChunk::new);
    }

    public PacketPlayOutUnloadChunk(FriendlyByteBuf buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeInt(chunkX)
                .writeInt(chunkZ)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutUnloadChunk(new FriendlyByteBuf(serialize()));
    }

}
