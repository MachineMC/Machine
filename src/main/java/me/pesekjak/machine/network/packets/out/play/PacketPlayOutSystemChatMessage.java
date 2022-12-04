package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSystemChatMessage extends PacketOut {

    private static final int ID = 0x62;

    @NotNull
    private Component message;
    private boolean overlay;

    static {
        register(PacketPlayOutSystemChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSystemChatMessage::new);
    }

    public PacketPlayOutSystemChatMessage(FriendlyByteBuf buf) {
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
