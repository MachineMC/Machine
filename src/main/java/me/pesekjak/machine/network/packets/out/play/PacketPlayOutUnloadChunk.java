package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

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
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutUnloadChunk(new FriendlyByteBuf(serialize()));
    }

}
