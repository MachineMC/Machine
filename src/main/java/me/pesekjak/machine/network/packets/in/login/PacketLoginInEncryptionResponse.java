package me.pesekjak.machine.network.packets.in.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketLoginInEncryptionResponse extends PacketIn {

    private static final int ID = 0x01;

    private byte[] secret;
    private byte[] verifyToken;
    private long salt;
    private byte[] messageSignature;

    static {
        register(PacketLoginInEncryptionResponse.class, ID, PacketState.LOGIN_IN,
                PacketLoginInEncryptionResponse::new);
    }

    public PacketLoginInEncryptionResponse(FriendlyByteBuf buf) {
        secret = buf.readByteArray();
        if(buf.readBoolean()) {
            verifyToken = buf.readByteArray();
        } else {
            salt = buf.readLong();
            messageSignature = buf.readByteArray();
        }
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.LOGIN_IN;
    }

    @Override
    public byte @NotNull [] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeByteArray(secret);
        if(verifyToken != null)
            buf.writeBoolean(true)
                    .writeByteArray(verifyToken);
        else if(salt != 0 && messageSignature != null)
            buf.writeBoolean(false)
                    .writeLong(salt)
                    .writeByteArray(messageSignature);
        return buf.bytes();
    }

    @Override
    public @NotNull PacketIn clone() {
        return new PacketLoginInEncryptionResponse(new FriendlyByteBuf(serialize()));
    }

}
