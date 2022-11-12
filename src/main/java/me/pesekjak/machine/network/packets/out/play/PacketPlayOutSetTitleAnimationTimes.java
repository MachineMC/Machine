package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

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
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeInt(fadeIn)
                .writeInt(stay)
                .writeInt(fadeOut)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetTitleAnimationTimes(new FriendlyByteBuf(serialize()));
    }

}
