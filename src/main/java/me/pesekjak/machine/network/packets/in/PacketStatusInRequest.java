package me.pesekjak.machine.network.packets.in;

import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;

public class PacketStatusInRequest extends PacketIn {

    public static final int ID = 0x00;

    static {
        PacketIn.register(PacketStatusInRequest.class, ID, PacketState.STATUS_IN,
                PacketStatusInRequest::new
        );
    }

    public PacketStatusInRequest(FriendlyByteBuf buf) {

    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new byte[1];
    }

    @Override
    public PacketIn clone() {
        return new PacketStatusInRequest(new FriendlyByteBuf(serialize()));
    }

}