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
                .writeDouble(diameter)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutBorderSize(new FriendlyByteBuf(serialize()));
    }

}
