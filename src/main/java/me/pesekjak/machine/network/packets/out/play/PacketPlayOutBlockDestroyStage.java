package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.world.BlockPosition;
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
