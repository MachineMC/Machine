package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

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

    public PacketPlayOutDisplayChatPreview(@NotNull ServerBuffer buf) {
        chatPreviewSetting = buf.readBoolean();
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
                .writeBoolean(chatPreviewSetting)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutDisplayChatPreview(new FriendlyByteBuf(serialize()));
    }

}
