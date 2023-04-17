package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

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

    public PacketPlayOutUnloadChunk(final ServerBuffer buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
    }

    @Override
    public int getId() {
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
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutUnloadChunk(new FriendlyByteBuf(serialize()));
    }

}
