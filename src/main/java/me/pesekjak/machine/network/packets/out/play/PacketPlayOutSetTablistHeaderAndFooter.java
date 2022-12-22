package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutSetTablistHeaderAndFooter extends PacketOut {

    private static final int ID = 0x63;

    @Getter @Setter
    private @NotNull Component header, footer;

    static {
        register(PacketPlayOutSetTablistHeaderAndFooter.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetTablistHeaderAndFooter::new);
    }

    public PacketPlayOutSetTablistHeaderAndFooter(@NotNull ServerBuffer buf) {
        header = buf.readComponent();
        footer = buf.readComponent();
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
                .writeComponent(header)
                .writeComponent(footer)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSetTablistHeaderAndFooter(new FriendlyByteBuf(serialize()));
    }

}
