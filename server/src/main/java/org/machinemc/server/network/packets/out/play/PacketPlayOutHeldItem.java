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
public class PacketPlayOutHeldItem extends PacketOut {

    private static final int ID = 0x4A;

    @Getter @Setter
    private byte slot;

    static {
        register(PacketPlayOutHeldItem.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutHeldItem::new);
    }

    public PacketPlayOutHeldItem(ServerBuffer buf) {
        slot = buf.readByte();
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
                .writeByte(slot)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutHeldItem(new FriendlyByteBuf(serialize()));
    }

}
