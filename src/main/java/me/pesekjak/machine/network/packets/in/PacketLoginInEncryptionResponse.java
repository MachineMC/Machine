package me.pesekjak.machine.network.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketLoginInEncryptionResponse extends PacketIn {

    private static final int ID = 0x01;

    @Getter @Setter
    private byte[] secret;
    @Getter @Setter
    private byte[] verifyToken;
    @Getter @Setter
    private long salt;
    @Getter @Setter
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
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
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
    public PacketIn clone() {
        return new PacketLoginInEncryptionResponse(new FriendlyByteBuf(serialize()));
    }

}
