package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class PacketPlayOutSetTitleText extends PacketOut {

    private static final int ID = 0x5D;

    @Getter @Setter
    private Component text;

    static {
        register(PacketPlayOutSetTitleText.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetTitleText::new);
    }

    public PacketPlayOutSetTitleText(FriendlyByteBuf buf) {
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
        return new PacketPlayOutSetTitleText(new FriendlyByteBuf(serialize()));
    }

}
