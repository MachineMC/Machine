package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PacketPlayOutUpdateTime extends PacketOut {

    private static final int ID = 0x5C;

    @Getter @Setter
    private long worldAge, timeOfDay;

    static {
        register(PacketPlayOutUpdateTime.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateTime::new);
    }

    public PacketPlayOutUpdateTime(@NotNull ServerBuffer buf) {
        worldAge = buf.readLong();
        timeOfDay = buf.readLong();
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
                .writeLong(worldAge)
                .writeLong(timeOfDay)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutUpdateTime(new FriendlyByteBuf(serialize()));
    }

}
