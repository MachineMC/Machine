package me.pesekjak.machine.network.packets.out;

import lombok.*;
import me.pesekjak.machine.auth.MessageSignature;
import me.pesekjak.machine.chat.ChatType;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
public class PacketPlayOutChatMessage extends PacketOut {

    private static final int ID = 0x33;

    @Getter @Setter @NotNull
    private Component signedMessage;
    @Getter @Setter @Nullable
    private Component unsignedMessage;
    @Getter @Setter
    private ChatType chatType;
    @Getter @Setter
    private UUID uuid;
    @Getter @Setter
    private Component displayName;
    @Getter @Setter @Nullable
    private Component teamName;
    @Getter @Setter
    private MessageSignature messageSignature;

    static {
        register(PacketPlayOutChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChatMessage::new);
    }

    public PacketPlayOutChatMessage(FriendlyByteBuf buf) {
        signedMessage = buf.readComponent();
        if(buf.readBoolean()) // has unsigned content
            unsignedMessage = buf.readComponent();
        chatType = ChatType.fromID(buf.readVarInt());
        uuid = buf.readUUID();
        displayName = buf.readComponent();
        if(buf.readBoolean()) // has team
            teamName = buf.readComponent();
        messageSignature = buf.readSignature();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeComponent(signedMessage)
                .writeBoolean(unsignedMessage != null);
        if(unsignedMessage != null)
            buf.writeComponent(unsignedMessage);
        buf.writeVarInt(chatType.getId())
                .writeUUID(uuid)
                .writeComponent(displayName)
                .writeBoolean(teamName != null);
        if(teamName != null)
            buf.writeComponent(teamName);
        return buf.writeSignature(messageSignature)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChatMessage(new FriendlyByteBuf(serialize()));
    }

}
