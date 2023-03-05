package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutCenterChunk extends PacketOut {

    private static final int ID = 0x4B;

    @Getter @Setter
    private int chunkX, chunkZ;

    static {
        register(PacketPlayOutCenterChunk.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutCenterChunk::new);
    }

    public PacketPlayOutCenterChunk(@NotNull ServerBuffer buf) {
        chunkX = buf.readVarInt();
        chunkZ = buf.readVarInt();
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
                .writeVarInt(chunkX)
                .writeVarInt(chunkZ)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutCenterChunk(new FriendlyByteBuf(serialize()));
    }

}
