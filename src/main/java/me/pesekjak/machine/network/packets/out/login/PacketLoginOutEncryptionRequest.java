package me.pesekjak.machine.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketLoginOutEncryptionRequest extends PacketOut {

    private static final int ID = 0x01;

    private final String serverID = SERVER_ID; // always same
    private byte @NotNull [] publicKey;
    private byte @NotNull [] verifyToken;

    static {
        register(PacketLoginOutEncryptionRequest.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutEncryptionRequest::new);
    }

    public PacketLoginOutEncryptionRequest(@NotNull ServerBuffer buf) {
        buf.readString(StandardCharsets.UTF_8); // reading serverID
        publicKey = buf.readByteArray();
        verifyToken = buf.readByteArray();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.LOGIN_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeString(serverID, StandardCharsets.UTF_8)
                .writeByteArray(publicKey)
                .writeByteArray(verifyToken)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketLoginOutEncryptionRequest(new FriendlyByteBuf(serialize()));
    }

    public static final String SERVER_ID = ""; // always empty

}
