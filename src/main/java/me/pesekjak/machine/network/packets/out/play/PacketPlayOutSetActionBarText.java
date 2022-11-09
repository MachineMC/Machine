package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutSetActionBarText extends PacketOut {

    private static final int ID = 0x43;

    @Getter @Setter @NotNull
    private Component actionBar;

    static {
        register(PacketPlayOutSetActionBarText.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetActionBarText::new);
    }

    public PacketPlayOutSetActionBarText(FriendlyByteBuf buf) {
        actionBar = buf.readComponent();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeComponent(actionBar)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetActionBarText(new FriendlyByteBuf(serialize()));
    }

}
