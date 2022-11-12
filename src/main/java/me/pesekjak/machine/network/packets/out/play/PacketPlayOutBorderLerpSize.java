package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutBorderLerpSize extends PacketOut {

    private static final int ID = 0x45;

    private double oldDiameter, newDiameter;
    private long speed;

    static {
        register(PacketPlayOutBorderLerpSize.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderLerpSize::new);
    }

    public PacketPlayOutBorderLerpSize(FriendlyByteBuf buf) {
        oldDiameter = buf.readDouble();
        newDiameter = buf.readDouble();
        speed = buf.readVarLong();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeDouble(oldDiameter)
                .writeDouble(newDiameter)
                .writeVarLong(speed)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBorderLerpSize(new FriendlyByteBuf(serialize()));
    }

}
