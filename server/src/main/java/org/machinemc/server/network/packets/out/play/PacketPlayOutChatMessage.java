package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.auth.MessageSignature;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.chat.ChatType;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutChatMessage extends PacketOut {

    private static final int ID = 0x33;

    private Component signedMessage;
    private @Nullable Component unsignedMessage;
    private ChatType chatType;
    private UUID uuid;
    private Component displayName;
    private @Nullable Component teamName;
    private MessageSignature messageSignature;

    static {
        register(PacketPlayOutChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChatMessage::new);
    }

    public PacketPlayOutChatMessage(ServerBuffer buf) {
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
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
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
