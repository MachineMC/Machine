package me.pesekjak.machine.network.packets.in;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class PacketLoginInStart extends PacketIn {

    public static final int ID = 0x00;

    @Getter @Setter @NotNull
    private String username;

    static {
        PacketIn.register(PacketLoginInStart.class, ID, PacketState.LOGIN_IN,
                PacketLoginInStart::new
        );
    }

    public PacketLoginInStart(FriendlyByteBuf buf) {
        username = buf.readString(StandardCharsets.UTF_8);
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(username, StandardCharsets.UTF_8)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketLoginInStart(new FriendlyByteBuf(serialize()));
    }

}
