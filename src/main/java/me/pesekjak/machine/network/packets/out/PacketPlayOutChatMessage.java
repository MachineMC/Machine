package me.pesekjak.machine.network.packets.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.chat.ChatType;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Data @EqualsAndHashCode(callSuper = false)
public class PacketPlayOutChatMessage extends PacketOut {

    private static final int ID = 0x33;

    @Getter
    private final String signedMessage;
    @Getter @Setter
    private boolean hasUnsignedContent;
    @Getter @Setter
    private String unsignedMessage;
    @Getter @Setter
    private ChatType chatType;
    @Getter @Setter
    private UUID uuid;
    @Getter @Setter
    private String displayName;
    @Getter @Setter
    private boolean hasTeamName;
    @Getter @Setter
    private String teamName;
    @Getter @Setter
    private long timestamp;
    @Getter @Setter
    private long salt;
    @Getter @Setter
    private byte[] signature;

    static {
        PacketOut.register(PacketPlayOutChatMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutChatMessage::new);
    }

    public PacketPlayOutChatMessage(FriendlyByteBuf buf) {
        signedMessage = buf.readString(StandardCharsets.UTF_8);
        hasUnsignedContent = buf.readBoolean();
        unsignedMessage = buf.readString(StandardCharsets.UTF_8);
        chatType = ChatType.fromId(buf.readVarInt());
        uuid = buf.readUUID();
        displayName = buf.readString(StandardCharsets.UTF_8);
        hasTeamName = buf.readBoolean();
        teamName = buf.readString(StandardCharsets.UTF_8);
        timestamp = buf.readLong();
        salt = buf.readLong();
        signature = buf.readByteArray();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(signedMessage, StandardCharsets.UTF_8)
                .writeBoolean(hasUnsignedContent)
                .writeString(unsignedMessage, StandardCharsets.UTF_8)
                .writeVarInt(chatType.id)
                .writeUUID(uuid)
                .writeString(displayName, StandardCharsets.UTF_8)
                .writeBoolean(hasTeamName)
                .writeString(teamName, StandardCharsets.UTF_8)
                .writeLong(timestamp)
                .writeLong(salt)
                .writeByteArray(signature)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutChatMessage(new FriendlyByteBuf(serialize()));
    }

}
