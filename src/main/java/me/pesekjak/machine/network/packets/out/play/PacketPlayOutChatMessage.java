package me.pesekjak.machine.network.packets.out.play;

import lombok.*;
import me.pesekjak.machine.auth.MessageSignatureImpl;
import me.pesekjak.machine.chat.ChatType;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutChatMessage extends PacketOut {

    private static final int ID = 0x33;

    @NotNull
    private Component signedMessage;
    @Nullable
    private Component unsignedMessage;
    @NotNull
    private ChatType chatType;
    @NotNull
    private UUID uuid;
    @NotNull
    private Component displayName;
    @Nullable
    private Component teamName;
    @NotNull
    private MessageSignatureImpl messageSignature;

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
    public @NotNull PacketOut clone() {
        return new PacketPlayOutChatMessage(new FriendlyByteBuf(serialize()));
    }

}
