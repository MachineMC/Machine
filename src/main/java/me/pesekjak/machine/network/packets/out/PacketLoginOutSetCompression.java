package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketLoginOutSetCompression extends PacketOut {

    private static final int ID = 0x03;

    @Getter @Setter
    private int threshold;

    static {
        PacketOut.register(PacketLoginOutSetCompression.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutSetCompression::new
        );
    }

    public PacketLoginOutSetCompression(FriendlyByteBuf buf) {
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
        return new PacketLoginOutSetCompression(new FriendlyByteBuf(serialize()));
    }

}
