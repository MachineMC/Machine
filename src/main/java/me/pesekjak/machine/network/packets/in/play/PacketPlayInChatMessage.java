package me.pesekjak.machine.network.packets.in.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.auth.MessageSignature;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayInChatMessage extends PacketIn {

    private static final int ID = 0x05;

    @NotNull
    private String message;
    @NotNull
    private MessageSignature messageSignature;

    static {
        register(PacketPlayInChatMessage.class, ID, PacketState.PLAY_IN,
                PacketPlayInChatMessage::new);
    }

    public PacketPlayInChatMessage(FriendlyByteBuf buf) {
        message = buf.readString();
        messageSignature = buf.readSignature();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(message)
                .writeSignature(messageSignature)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketPlayInChatMessage(new FriendlyByteBuf(serialize()));
    }
}
