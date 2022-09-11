package me.pesekjak.machine.network.packets.out;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class PacketPlayOutSystemChatMessage extends PacketOut {

    private static final int ID = 0x62;

    @Getter @Setter @NotNull
    private String json;
    @Getter @Setter
    private boolean overlay;

    static {
        PacketOut.register(PacketPlayOutSystemChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSystemChatMessage::new);
    }

    public PacketPlayOutSystemChatMessage(FriendlyByteBuf buf) {
        json = buf.readString(StandardCharsets.UTF_8);
        overlay = buf.readBoolean();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(json, StandardCharsets.UTF_8)
                .writeBoolean(overlay)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSystemChatMessage(new FriendlyByteBuf(serialize()));
    }
}
