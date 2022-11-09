package me.pesekjak.machine.network.packets.in.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.auth.PublicKeyData;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketLoginInStart extends PacketIn {

    private static final int ID = 0x00;

    @NotNull
    private String username;
    @Nullable
    private PublicKeyData publicKeyData;
    @Nullable
    private UUID uuid;

    static {
        register(PacketLoginInStart.class, ID, PacketState.LOGIN_IN,
                PacketLoginInStart::new
        );
    }

    public PacketLoginInStart(FriendlyByteBuf buf) {
        username = buf.readString();
        if (buf.readBoolean())
            publicKeyData = buf.readPublicKey();
        if (buf.readBoolean())
            uuid = buf.readUUID();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeString(username)
                .writeBoolean(publicKeyData != null);
        if (publicKeyData != null)
            publicKeyData.write(buf);
        buf.writeBoolean(uuid != null);
        if (uuid != null)
            buf.writeUUID(uuid);
        return buf.bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketLoginInStart(new FriendlyByteBuf(serialize()));
    }

}
