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
public class PacketPlayOutResourcePack extends PacketOut {

    private static final int ID = 0x3D;

    @NotNull
    private String url;
    @NotNull
    private String hash;
    private boolean forced;
    @Nullable
    private Component promptMessage;

    static {
        register(PacketPlayOutResourcePack.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutResourcePack::new);
    }

    public PacketPlayOutResourcePack(FriendlyByteBuf buf) {
        url = buf.readString(StandardCharsets.UTF_8);
        hash = buf.readString(StandardCharsets.UTF_8);
        forced = buf.readBoolean();
        if (buf.readBoolean())
            promptMessage = buf.readComponent();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeString(url, StandardCharsets.UTF_8)
                .writeString(hash, StandardCharsets.UTF_8)
                .writeBoolean(forced)
                .writeBoolean(promptMessage != null);
        if (promptMessage != null)
            buf.writeComponent(promptMessage);
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutResourcePack(new FriendlyByteBuf(serialize()));
    }

}
