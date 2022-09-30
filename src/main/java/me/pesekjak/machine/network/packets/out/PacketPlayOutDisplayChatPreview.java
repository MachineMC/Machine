package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutDisplayChatPreview extends PacketOut {

    private static final int ID = 0x4E;

    @Getter @Setter
    private boolean chatPreviewSetting;

    static {
        register(PacketPlayOutDisplayChatPreview.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutDisplayChatPreview::new);
    }

    public PacketPlayOutDisplayChatPreview(FriendlyByteBuf buf) {
        chatPreviewSetting = buf.readBoolean();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeBoolean(chatPreviewSetting)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutDisplayChatPreview(new FriendlyByteBuf(serialize()));
    }

}
