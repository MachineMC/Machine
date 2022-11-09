package me.pesekjak.machine.network.packets.out.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketStatusOutPong extends PacketOut {

    private static final int ID = 0x01;

    @Getter @Setter
    private long payload;

    static {
        register(PacketStatusOutPong.class, ID, PacketState.STATUS_OUT,
                PacketStatusOutPong::new
        );
    }

    public PacketStatusOutPong(FriendlyByteBuf buf) {
        payload = buf.readLong();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeLong(payload)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketStatusOutPong(new FriendlyByteBuf(serialize()));
    }

}
