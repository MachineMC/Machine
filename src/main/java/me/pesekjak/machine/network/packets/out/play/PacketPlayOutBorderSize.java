package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutBorderSize extends PacketOut {

    private static final int ID = 0x46;

    @Getter @Setter
    private double diameter;

    static {
        register(PacketPlayOutBorderSize.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderSize::new);
    }

    public PacketPlayOutBorderSize(FriendlyByteBuf buf) {
        diameter = buf.readDouble();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeDouble(diameter)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBorderSize(new FriendlyByteBuf(serialize()));
    }

}
