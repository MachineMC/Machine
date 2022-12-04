package me.pesekjak.machine.network.packets.out.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
public class PacketStatusOutResponse extends PacketOut {

    private static final int ID = 0x00;

    static {
        register(PacketStatusOutResponse.class, ID, PacketState.STATUS_OUT,
                PacketStatusOutResponse::new
        );
    }

    @Getter @Setter
    private String json;

    public PacketStatusOutResponse(FriendlyByteBuf buf) {
        json = buf.readString(StandardCharsets.UTF_8);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.STATUS_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeString(json, StandardCharsets.UTF_8)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketStatusOutResponse(new FriendlyByteBuf(serialize()));
    }

}
