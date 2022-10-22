package me.pesekjak.machine.network.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketStatusInPing extends PacketIn {

    private static final int ID = 0x01;

    @Getter @Setter
    private long payload;

    static {
        register(PacketStatusInPing.class, ID, PacketState.STATUS_IN,
                PacketStatusInPing::new
        );
    }

    public PacketStatusInPing(FriendlyByteBuf buf) {
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
    public PacketIn clone() {
        return new PacketStatusInPing(new FriendlyByteBuf(serialize()));
    }

}
