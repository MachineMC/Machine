package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutServerData extends PacketOut {

    private static final int ID = 0x42;

    private @Nullable Component motd;
    private @Nullable String icon;
    private boolean previewsChat, enforcedSecureChat;


    static {
        register(PacketPlayOutServerData.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutServerData::new);
    }

    public PacketPlayOutServerData(@NotNull ServerBuffer buf) {
        if (buf.readBoolean())
            motd = buf.readComponent();
        if (buf.readBoolean())
            icon = buf.readString(StandardCharsets.UTF_8);
        previewsChat = buf.readBoolean();
        enforcedSecureChat = buf.readBoolean();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeBoolean(motd != null);
        if (motd != null)
            buf.writeComponent(motd);
        buf.writeBoolean(icon != null);
        if (icon != null)
            buf.writeString(icon, StandardCharsets.UTF_8);
        return buf.writeBoolean(previewsChat)
                .writeBoolean(enforcedSecureChat)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutServerData(new FriendlyByteBuf(serialize()));
    }

}
