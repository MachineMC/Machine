package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class PacketPlayOutServerData extends PacketOut {

    private static final int ID = 0x42;

    @Getter @Setter @Nullable
    private Component motd;
    @Getter @Setter @Nullable
    private String icon;
    @Getter @Setter
    private boolean previewsChat, enforcedSecureChat;


    static {
        register(PacketPlayOutServerData.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutServerData::new);
    }

    public PacketPlayOutServerData(FriendlyByteBuf buf) {
        if (buf.readBoolean())
            motd = buf.readComponent();
        if (buf.readBoolean())
            icon = buf.readString();
        previewsChat = buf.readBoolean();
        enforcedSecureChat = buf.readBoolean();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeBoolean(motd != null);
        if (motd != null)
            buf.writeComponent(motd);
        buf.writeBoolean(icon != null);
        if (icon != null)
            buf.writeString(icon);
        return buf.writeBoolean(previewsChat)
                .writeBoolean(enforcedSecureChat)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutServerData(new FriendlyByteBuf(serialize()));
    }

}
