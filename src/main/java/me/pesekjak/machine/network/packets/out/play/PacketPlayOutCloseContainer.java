package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutCloseContainer extends PacketOut {

    private static final int ID = 0x10;

    @Getter @Setter
    private byte windowId;

    static {
        register(PacketPlayOutCloseContainer.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutCloseContainer::new);
    }

    public PacketPlayOutCloseContainer(FriendlyByteBuf buf) {
        windowId = buf.readByte();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(windowId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutCloseContainer(new FriendlyByteBuf(serialize()));
    }

}
