package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutBorderWarningDelay extends PacketOut {

    private static final int ID = 0x47;

    @Getter @Setter
    private int warningTime;

    static {
        register(PacketPlayOutBorderWarningDelay.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderWarningDelay::new);
    }

    public PacketPlayOutBorderWarningDelay(FriendlyByteBuf buf) {
        warningTime = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(warningTime)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBorderWarningDelay(new FriendlyByteBuf(serialize()));
    }

}
