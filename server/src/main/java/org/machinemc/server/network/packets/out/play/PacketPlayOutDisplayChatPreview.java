package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutDisplayChatPreview extends PacketOut {

    private static final int ID = 0x4E;

    @Getter @Setter
    private boolean chatPreviewSetting;

    static {
        register(PacketPlayOutDisplayChatPreview.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutDisplayChatPreview::new);
    }

    public PacketPlayOutDisplayChatPreview(ServerBuffer buf) {
        chatPreviewSetting = buf.readBoolean();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
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
