package me.pesekjak.machine.network.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@AllArgsConstructor
public class PacketPlayInChatMessage extends PacketIn {

    private static final int ID = 0x05;

    @Getter @Setter
    private String message;
    @Getter @Setter
    private Instant timestamp;
    @Getter @Setter
    private long salt;
    @Getter @Setter
    private byte[] signature;
    @Getter @Setter
    private boolean signedPreview;

    static {
        PacketIn.register(PacketPlayInChatMessage.class, ID, PacketState.PLAY_IN,
                PacketPlayInChatMessage::new);
    }

    public PacketPlayInChatMessage(FriendlyByteBuf buf) {
        message = buf.readString(StandardCharsets.UTF_8);
        timestamp = buf.readInstant();
        salt = buf.readLong();
        signature = buf.readByteArray();
        signedPreview = buf.readBoolean();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(message, StandardCharsets.UTF_8)
                .writeInstant(timestamp)
                .writeLong(salt)
                .writeByteArray(signature)
                .writeBoolean(signedPreview)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketPlayInChatMessage(new FriendlyByteBuf(serialize()));
    }
}
