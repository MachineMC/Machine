package me.pesekjak.machine.network.packets.out.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketStatusOutResponse extends PacketOut {

    private static final int ID = 0x00;

    static {
        register(PacketStatusOutResponse.class, ID, PacketState.STATUS_OUT,
                PacketStatusOutResponse::new
        );
    }

    @Getter @Setter
    private String json;

    public PacketStatusOutResponse(FriendlyByteBuf buf) {
        json = buf.readString();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(json)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketStatusOutResponse(new FriendlyByteBuf(serialize()));
    }

}
