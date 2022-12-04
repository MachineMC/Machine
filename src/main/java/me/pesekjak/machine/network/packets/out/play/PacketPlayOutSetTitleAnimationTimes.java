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
public class PacketPlayOutSetTitleAnimationTimes extends PacketOut {

    private static final int ID = 0x5E;

    @Getter @Setter
    private int fadeIn, stay, fadeOut;

    static {
        register(PacketPlayOutSetTitleAnimationTimes.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetTitleAnimationTimes::new);
    }

    public PacketPlayOutSetTitleAnimationTimes(FriendlyByteBuf buf) {
        fadeIn = buf.readInt();
        stay = buf.readInt();
        fadeOut = buf.readInt();
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
                .writeInt(fadeIn)
                .writeInt(stay)
                .writeInt(fadeOut)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSetTitleAnimationTimes(new FriendlyByteBuf(serialize()));
    }

}
