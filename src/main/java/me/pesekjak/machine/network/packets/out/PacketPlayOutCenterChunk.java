package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class PacketPlayOutCenterChunk extends PacketOut {

    private static final int ID = 0x4B;

    @Getter @Setter
    private int chunkX, chunkZ;

    static {
        register(PacketPlayOutCenterChunk.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutCenterChunk::new);
    }

    public PacketPlayOutCenterChunk(FriendlyByteBuf buf) {
        chunkX = buf.readVarInt();
        chunkZ = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(chunkX)
                .writeVarInt(chunkZ)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutCenterChunk(new FriendlyByteBuf(serialize()));
    }

}
