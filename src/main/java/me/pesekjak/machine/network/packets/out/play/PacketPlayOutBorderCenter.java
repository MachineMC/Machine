package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

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
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeDouble(x)
                .writeDouble(z)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutBorderCenter(new FriendlyByteBuf(serialize()));
    }

}
