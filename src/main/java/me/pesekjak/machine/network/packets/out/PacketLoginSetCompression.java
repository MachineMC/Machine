package me.pesekjak.machine.network.packets.out;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

public class PacketLoginSetCompression extends PacketOut {

    public static final int ID = 0x03;

    @Getter @Setter
    private int threshold;

    static {
        PacketOut.register(PacketLoginSetCompression.class, ID, PacketState.LOGIN_OUT,
                PacketLoginSetCompression::new
        );
    }

    public PacketLoginSetCompression(FriendlyByteBuf buf) {
        threshold = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(threshold)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginSetCompression(new FriendlyByteBuf(serialize()));
    }

}
