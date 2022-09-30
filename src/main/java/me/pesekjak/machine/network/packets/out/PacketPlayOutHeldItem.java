package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutHeldItem extends PacketOut {

    private static final int ID = 0x4A;

    @Getter @Setter
    private byte slot;

    static {
        register(PacketPlayOutHeldItem.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutHeldItem::new);
    }

    public PacketPlayOutHeldItem(FriendlyByteBuf buf) {
        slot = buf.readByte();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(slot)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutHeldItem(new FriendlyByteBuf(serialize()));
    }

}
