package me.pesekjak.machine.network.packets.in.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.auth.MessageSignatureImpl;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayInChatMessage extends PacketIn {

    private static final int ID = 0x05;

    @NotNull
    private String message;
    @NotNull
    private MessageSignatureImpl messageSignature;

    static {
        register(PacketPlayInChatMessage.class, ID, PacketState.PLAY_IN,
                PacketPlayInChatMessage::new);
    }

    public PacketPlayInChatMessage(FriendlyByteBuf buf) {
        message = buf.readString(StandardCharsets.UTF_8);
        messageSignature = buf.readSignature();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_IN;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeString(message, StandardCharsets.UTF_8)
                .writeSignature(messageSignature)
                .bytes();
    }

    @Override
    public @NotNull PacketIn clone() {
        return new PacketPlayInChatMessage(new FriendlyByteBuf(serialize()));
    }
}
