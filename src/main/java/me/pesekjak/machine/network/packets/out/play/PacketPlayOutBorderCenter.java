package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutBorderCenter extends PacketOut {

    private static final int ID = 0x44;

    @Getter @Setter
    private double x, z;

    static {
        register(PacketPlayOutBorderCenter.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderCenter::new);
    }

    public PacketPlayOutBorderCenter(FriendlyByteBuf buf) {
        x = buf.readDouble();
        z = buf.readDouble();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeDouble(x)
                .writeDouble(z)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBorderCenter(new FriendlyByteBuf(serialize()));
    }

}
