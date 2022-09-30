package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.BlockPosition;

@AllArgsConstructor
public class PacketPlayOutBlockDestroyStage extends PacketOut {

    private static final int ID = 0x06;

    @Getter
    private int entityId;
    @Getter @Setter
    private BlockPosition position;
    @Getter @Setter
    private byte destroyStage;

    static {
        register(PacketPlayOutBlockDestroyStage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBlockDestroyStage::new);
    }

    public PacketPlayOutBlockDestroyStage(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        position = buf.readBlockPos();
        destroyStage = buf.readByte();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeBlockPos(position)
                .writeByte(destroyStage)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBlockDestroyStage(new FriendlyByteBuf(serialize()));
    }

}
