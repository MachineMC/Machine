package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutClearTitles extends PacketOut {

    private static final int ID = 0x0D;

    @Getter @Setter
    private boolean reset;

    static {
        register(PacketPlayOutClearTitles.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutClearTitles::new);
    }

    public PacketPlayOutClearTitles(FriendlyByteBuf buf) {
        reset = buf.readBoolean();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeBoolean(reset)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutClearTitles(new FriendlyByteBuf(serialize()));
    }

}
