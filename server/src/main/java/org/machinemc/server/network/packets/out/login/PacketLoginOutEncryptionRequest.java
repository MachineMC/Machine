package org.machinemc.server.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

import java.nio.charset.StandardCharsets;

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

    public PacketLoginOutEncryptionRequest(final ServerBuffer buf) {
        buf.readString(StandardCharsets.UTF_8); // reading serverID
        publicKey = buf.readByteArray();
        verifyToken = buf.readByteArray();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.LOGIN_OUT;
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
