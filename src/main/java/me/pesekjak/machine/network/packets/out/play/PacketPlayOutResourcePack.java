package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutResourcePack extends PacketOut {

    private static final int ID = 0x3D;

    private @NotNull String url;
    private @NotNull String hash;
    private boolean forced;
    private @Nullable Component promptMessage;

    static {
        register(PacketPlayOutResourcePack.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutResourcePack::new);
    }

    public PacketPlayOutResourcePack(@NotNull ServerBuffer buf) {
        url = buf.readString(StandardCharsets.UTF_8);
        hash = buf.readString(StandardCharsets.UTF_8);
        forced = buf.readBoolean();
        if (buf.readBoolean())
            promptMessage = buf.readComponent();
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
                .writeString(url, StandardCharsets.UTF_8)
                .writeString(hash, StandardCharsets.UTF_8)
                .writeBoolean(forced)
                .writeBoolean(promptMessage != null);
        if (promptMessage != null)
            buf.writeComponent(promptMessage);
        return buf.bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutResourcePack(new FriendlyByteBuf(serialize()));
    }

}
