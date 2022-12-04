package me.pesekjak.machine.network.packets.in.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketIn;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayInKeepAlive extends PacketIn {

    private final static int ID = 0x12;

    @Getter @Setter
    private long keepAliveId;

    static {
        register(PacketPlayInKeepAlive.class, ID, PacketState.PLAY_IN,
                PacketPlayInKeepAlive::new);
    }

    public PacketPlayInKeepAlive(FriendlyByteBuf buf) {
        keepAliveId = buf.readLong();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_IN;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeLong(keepAliveId)
                .bytes();
    }

    @Override
    public @NotNull PacketIn clone() {
        return new PacketPlayInKeepAlive(new FriendlyByteBuf(serialize()));
    }

}
