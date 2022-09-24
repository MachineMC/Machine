package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class PacketLoginOutEncryptionRequest extends PacketOut {

    private static final int ID = 0x01;

    @SuppressWarnings("FieldCanBeLocal")
    private final String serverID = SERVER_ID; // always same
    @Getter @Setter
    private byte[] publicKey;
    @Getter @Setter
    private byte[] verifyToken;

    static {
        register(PacketLoginOutEncryptionRequest.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutEncryptionRequest::new);
    }

    public PacketLoginOutEncryptionRequest(FriendlyByteBuf buf) {
        buf.readString(StandardCharsets.UTF_8); // reading serverID
        publicKey = buf.readByteArray();
        verifyToken = buf.readByteArray();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(serverID, StandardCharsets.UTF_8)
                .writeByteArray(publicKey)
                .writeByteArray(verifyToken)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginOutEncryptionRequest(new FriendlyByteBuf(serialize()));
    }

    public static final String SERVER_ID = ""; // always empty

}
