package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class PacketPlayOutSetSubtitleText extends PacketOut {

    private static final int ID = 0x5B;

    @Getter @Setter
    private Component text;

    static {
        register(PacketPlayOutSetSubtitleText.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetSubtitleText::new);
    }

    public PacketPlayOutSetSubtitleText(FriendlyByteBuf buf) {
        text = buf.readComponent();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeComponent(text)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetSubtitleText(new FriendlyByteBuf(serialize()));
    }

}
