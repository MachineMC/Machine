package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutKeepAlive extends PacketOut {

    private final static int ID = 0x20;

    @Getter @Setter
    private long keepAliveId;

    static {
        register(PacketPlayOutKeepAlive.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutKeepAlive::new);
    }

    public PacketPlayOutKeepAlive(FriendlyByteBuf buf) {
        keepAliveId = buf.readLong();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeLong(keepAliveId)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutKeepAlive(new FriendlyByteBuf(serialize()));
    }

}
