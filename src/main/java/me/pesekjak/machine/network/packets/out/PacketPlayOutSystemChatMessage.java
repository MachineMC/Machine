package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class PacketPlayOutSystemChatMessage extends PacketOut {

    private static final int ID = 0x62;

    @Getter @Setter @NotNull
    private Component message;
    @Getter @Setter
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
    public int getID() {
        return ID;
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
