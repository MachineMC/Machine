package me.pesekjak.machine.network.packets.in.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketStatusInPing extends PacketIn {

    private static final int ID = 0x01;

    @Getter @Setter
    private long payload;

    static {
        register(PacketStatusInPing.class, ID, PacketState.STATUS_IN,
                PacketStatusInPing::new
        );
    }

    public PacketStatusInPing(FriendlyByteBuf buf) {
        payload = buf.readLong();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.STATUS_IN;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeLong(payload)
                .bytes();
    }

    @Override
    public @NotNull PacketIn clone() {
        return new PacketStatusInPing(new FriendlyByteBuf(serialize()));
    }

}
