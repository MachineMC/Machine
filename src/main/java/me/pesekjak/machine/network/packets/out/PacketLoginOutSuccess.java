package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
public class PacketLoginOutSuccess extends PacketOut {

    public static int ID = 0x02;

    @Getter @Setter @NotNull
    private UUID uuid;
    @Getter @Setter @NotNull
    private String userName;
    @Getter @Setter
    private int properties;

    static {
        PacketOut.register(PacketLoginOutSuccess.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutSuccess::new
        );
    }

    public PacketLoginOutSuccess(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
        userName = buf.readString(StandardCharsets.UTF_8);
        properties = buf.readVarInt();
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
        return new PacketLoginOutSuccess(new FriendlyByteBuf(serialize()));
    }

}
