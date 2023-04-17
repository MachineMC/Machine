package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

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

    public PacketPlayOutUpdateTime(final ServerBuffer buf) {
        worldAge = buf.readLong();
        timeOfDay = buf.readLong();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
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
