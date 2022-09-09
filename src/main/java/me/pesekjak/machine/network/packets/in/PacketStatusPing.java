package me.pesekjak.machine.network.packets.in;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;

public class PacketStatusPing extends PacketIn {

    public static final int ID = 0x01;

    @Getter @Setter
    private long payload;

    static {
        PacketIn.register(PacketStatusPing.class, ID, PacketState.STATUS_IN,
                PacketStatusPing::new
        );
    }

    public PacketStatusPing(FriendlyByteBuf buf) {
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
        return new PacketStatusPing(new FriendlyByteBuf(serialize()));
    }

}
