package me.pesekjak.machine.network.packets.in;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;

public class PacketStatusInPing extends PacketIn {

    public static final int ID = 0x01;

    @Getter @Setter
    private long payload;

    static {
        PacketIn.register(PacketStatusInPing.class, ID, PacketState.STATUS_IN,
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
