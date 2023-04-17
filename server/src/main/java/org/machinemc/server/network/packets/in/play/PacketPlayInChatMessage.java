package org.machinemc.server.network.packets.in.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.auth.MessageSignature;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayInChatMessage extends PacketIn {

    private static final int ID = 0x05;

    private String message;
    private MessageSignature messageSignature;

    static {
        register(PacketPlayInChatMessage.class, ID, PacketState.PLAY_IN,
                PacketPlayInChatMessage::new);
    }

    public PacketPlayInChatMessage(final ServerBuffer buf) {
        message = buf.readString(StandardCharsets.UTF_8);
        messageSignature = buf.readSignature();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_IN;
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
