package me.pesekjak.machine.network.packets.out;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketLoginSuccess extends PacketOut {

    public static int ID = 0x02;

    @Getter @Setter @NotNull
    private UUID uuid;
    @Getter @Setter @NotNull
    private String userName;
    @SuppressWarnings("FieldCanBeLocal")
    private final int properties = 0; // TODO edit for online-mode

    static {
        PacketOut.register(PacketLoginSuccess.class, ID, PacketState.LOGIN_OUT,
                PacketLoginSuccess::new
        );
    }

    public PacketLoginSuccess(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
        userName = buf.readString(StandardCharsets.UTF_8);
        buf.readVarInt(); // reading properties
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeUUID(uuid)
                .writeString(userName, StandardCharsets.UTF_8)
                .writeVarInt(properties)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginSuccess(new FriendlyByteBuf(serialize()));
    }

}
