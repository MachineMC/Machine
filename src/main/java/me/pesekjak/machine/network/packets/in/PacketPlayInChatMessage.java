package me.pesekjak.machine.network.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.auth.MessageSignature;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class PacketPlayInChatMessage extends PacketIn {

    private static final int ID = 0x05;

    @Getter @Setter
    private String message;
    @Getter @Setter
    private MessageSignature messageSignature;

    static {
        register(PacketPlayInChatMessage.class, ID, PacketState.PLAY_IN,
                PacketPlayInChatMessage::new);
    }

    public PacketPlayInChatMessage(FriendlyByteBuf buf) {
        message = buf.readString(StandardCharsets.UTF_8);
        messageSignature = buf.readSignature();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(message, StandardCharsets.UTF_8)
                .writeSignature(messageSignature)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketPlayInChatMessage(new FriendlyByteBuf(serialize()));
    }
}
