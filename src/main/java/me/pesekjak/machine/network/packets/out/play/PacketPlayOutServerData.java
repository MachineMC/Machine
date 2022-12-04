package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutServerData extends PacketOut {

    private static final int ID = 0x42;

    @Nullable
    private Component motd;
    @Nullable
    private String icon;
    private boolean previewsChat, enforcedSecureChat;


    static {
        register(PacketPlayOutServerData.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutServerData::new);
    }

    public PacketPlayOutServerData(FriendlyByteBuf buf) {
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
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
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
