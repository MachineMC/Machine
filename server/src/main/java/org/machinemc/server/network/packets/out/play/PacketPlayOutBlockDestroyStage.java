package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockPosition;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutBlockDestroyStage extends PacketOut {

    private static final int ID = 0x06;

    private int entityId;
    private @NotNull BlockPosition position;
    private byte destroyStage;

    static {
        register(PacketPlayOutBlockDestroyStage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBlockDestroyStage::new);
    }

    public PacketPlayOutBlockDestroyStage(@NotNull ServerBuffer buf) {
        entityId = buf.readVarInt();
        position = buf.readBlockPos();
        destroyStage = buf.readByte();
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
                .writeVarInt(entityId)
                .writeBlockPos(position)
                .writeByte(destroyStage)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutBlockDestroyStage(new FriendlyByteBuf(serialize()));
    }

}
