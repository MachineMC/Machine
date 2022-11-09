package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutBorderWarningDistance extends PacketOut {

    private static final int ID = 0x48;

    @Getter @Setter
    private int warningBlocks;

    static {
        register(PacketPlayOutBorderWarningDistance.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutBorderWarningDistance::new);
    }

    public PacketPlayOutBorderWarningDistance(FriendlyByteBuf buf) {
        warningBlocks = buf.readVarInt();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(warningBlocks)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutBorderWarningDistance(new FriendlyByteBuf(serialize()));
    }

}
