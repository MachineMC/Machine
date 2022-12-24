package me.pesekjak.machine.network.packets.out.play;

import lombok.*;
import me.pesekjak.machine.auth.MessageSignature;
import me.pesekjak.machine.chat.ChatType;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutChatMessage extends PacketOut {

    private static final int ID = 0x33;

    private @NotNull Component signedMessage;
    private @Nullable Component unsignedMessage;
    private @NotNull ChatType chatType;
    private @NotNull UUID uuid;
    private @NotNull Component displayName;
    private @Nullable Component teamName;
    private @NotNull MessageSignature messageSignature;

    static {
        register(PacketPlayOutChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChatMessage::new);
    }

    public PacketPlayOutChatMessage(@NotNull ServerBuffer buf) {
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
