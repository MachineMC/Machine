package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutClearTitles extends PacketOut {

    private static final int ID = 0x0D;

    @Getter @Setter
    private boolean reset;

    static {
        register(PacketPlayOutClearTitles.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutClearTitles::new);
    }

    public PacketPlayOutClearTitles(@NotNull ServerBuffer buf) {
        reset = buf.readBoolean();
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
                .writeBoolean(reset)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutClearTitles(new FriendlyByteBuf(serialize()));
    }

}
