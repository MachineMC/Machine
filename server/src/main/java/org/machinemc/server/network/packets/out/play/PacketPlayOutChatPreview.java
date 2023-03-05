package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutChatPreview extends PacketOut {

    private static final int ID = 0x0C;

    private int queryId;
    private @Nullable Component preview;

    static {
        register(PacketPlayOutChatPreview.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutChatPreview::new);
    }

    public PacketPlayOutChatPreview(ServerBuffer buf) {
        queryId = buf.readVarInt();
        if (buf.readBoolean())
            preview = buf.readComponent();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
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
