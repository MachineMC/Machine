package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class PacketPlayOutChatPreview extends PacketOut {

    private static final int ID = 0x0C;

    @Getter
    private int queryId;
    @Getter @Setter @Nullable
    private Component preview;

    static {
        register(PacketPlayOutChatPreview.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChatPreview::new);
    }

    public PacketPlayOutChatPreview(FriendlyByteBuf buf) {
        queryId = buf.readVarInt();
        if (buf.readBoolean())
            preview = buf.readComponent();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeInt(queryId)
                .writeBoolean(preview != null);
        if (preview != null) {
            buf.writeComponent(preview);
        }
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChatPreview(new FriendlyByteBuf(serialize()));
    }

}
