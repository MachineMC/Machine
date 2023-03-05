package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSystemChatMessage extends PacketOut {

    private static final int ID = 0x62;

    private Component message;
    private boolean overlay;

    static {
        register(PacketPlayOutSystemChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSystemChatMessage::new);
    }

    public PacketPlayOutSystemChatMessage(ServerBuffer buf) {
        message = buf.readComponent();
        overlay = buf.readBoolean();
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
                .writeComponent(message)
                .writeBoolean(overlay)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSystemChatMessage(new FriendlyByteBuf(serialize()));
    }

}
