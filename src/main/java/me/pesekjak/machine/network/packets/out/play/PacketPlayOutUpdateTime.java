package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;

@AllArgsConstructor
public class PacketPlayOutUpdateTime extends PacketOut {

    private static final int ID = 0x5C;

    @Getter @Setter
    private long worldAge, timeOfDay;

    static {
        register(PacketPlayOutUpdateTime.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutUpdateTime::new);
    }

    public PacketPlayOutUpdateTime(FriendlyByteBuf buf) {
        worldAge = buf.readLong();
        timeOfDay = buf.readLong();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeLong(worldAge)
                .writeLong(timeOfDay)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutUpdateTime(new FriendlyByteBuf(serialize()));
    }

}
