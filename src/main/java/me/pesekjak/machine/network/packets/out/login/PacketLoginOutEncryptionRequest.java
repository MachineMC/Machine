package me.pesekjak.machine.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketLoginOutEncryptionRequest extends PacketOut {

    private static final int ID = 0x01;

    private final String serverID = SERVER_ID; // always same
    private byte[] publicKey;
    private byte[] verifyToken;

    static {
        register(PacketLoginOutEncryptionRequest.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutEncryptionRequest::new);
    }

    public PacketLoginOutEncryptionRequest(FriendlyByteBuf buf) {
        buf.readString(); // reading serverID
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
                .writeString(serverID)
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
