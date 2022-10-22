package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutKeepAlive extends PacketOut {

    private final static int ID = 0x20;

    @Getter @Setter
    private long keepAliveId;

    static {
        register(PacketPlayOutKeepAlive.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutKeepAlive::new);
    }

    public PacketPlayOutKeepAlive(FriendlyByteBuf buf) {
        keepAliveId = buf.readLong();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeLong(keepAliveId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutKeepAlive(new FriendlyByteBuf(serialize()));
    }

}
