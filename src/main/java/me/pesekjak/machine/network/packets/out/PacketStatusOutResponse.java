package me.pesekjak.machine.network.packets.out;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class PacketStatusOutResponse extends PacketOut {

    public static final int ID = 0x00;

    static {
        PacketOut.register(PacketStatusOutResponse.class, ID, PacketState.STATUS_OUT,
                PacketStatusOutResponse::new
        );
    }

    @Getter @Setter
    private String json;

    public PacketStatusOutResponse(FriendlyByteBuf buf) {
        json = buf.readString(StandardCharsets.UTF_8);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(json, StandardCharsets.UTF_8)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketStatusOutResponse(new FriendlyByteBuf(serialize()));
    }

}