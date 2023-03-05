package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSystemChatMessage extends PacketOut {

    private static final int ID = 0x62;

    private @NotNull Component message;
    private boolean overlay;

    static {
        register(PacketPlayOutSystemChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSystemChatMessage::new);
    }

    public PacketPlayOutSystemChatMessage(@NotNull ServerBuffer buf) {
        message = buf.readComponent();
        overlay = buf.readBoolean();
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
                .writeComponent(message)
                .writeBoolean(overlay)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSystemChatMessage(new FriendlyByteBuf(serialize()));
    }

}
