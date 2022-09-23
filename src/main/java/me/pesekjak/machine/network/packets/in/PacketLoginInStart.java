package me.pesekjak.machine.network.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.auth.PublicKeyData;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
public class PacketLoginInStart extends PacketIn {

    private static final int ID = 0x00;

    @Getter @Setter @NotNull
    private String username;
    @Getter @Setter @Nullable
    private PublicKeyData publicKeyData;
    @Getter @Setter @Nullable
    private UUID uuid;

    static {
        PacketIn.register(PacketLoginInStart.class, ID, PacketState.LOGIN_IN,
                PacketLoginInStart::new
        );
    }

    public PacketLoginInStart(FriendlyByteBuf buf) {
        username = buf.readString(StandardCharsets.UTF_8);
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
                .writeString(username, StandardCharsets.UTF_8)
                .writeBoolean(publicKeyData != null);
        if (publicKeyData != null)
            buf.write(publicKeyData);
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
