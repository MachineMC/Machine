package me.pesekjak.machine.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketLoginOutSetCompression extends PacketOut {

    private static final int ID = 0x03;

    @Getter @Setter
    private int threshold;

    static {
        register(PacketLoginOutSetCompression.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutSetCompression::new
        );
    }

    public PacketLoginOutSetCompression(FriendlyByteBuf buf) {
        threshold = buf.readVarInt();
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
                .writeVarInt(threshold)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketLoginOutSetCompression(new FriendlyByteBuf(serialize()));
    }

}
