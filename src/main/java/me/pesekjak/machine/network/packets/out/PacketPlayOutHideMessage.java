package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutHideMessage extends PacketOut {

    private static final int ID = 0x18;

    @Getter @Setter
    private byte[] signature;

    static {
        register(PacketPlayOutHideMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutHideMessage::new);
    }

    public PacketPlayOutHideMessage(FriendlyByteBuf buf) {
        signature = buf.readByteArray();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByteArray(signature)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutHideMessage(new FriendlyByteBuf(serialize()));
    }

}
