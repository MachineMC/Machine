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
public class PacketPlayOutKeepAlive extends PacketOut {

    private final static int ID = 0x20;

    @Getter @Setter
    private long keepAliveId;

    static {
        register(PacketPlayOutKeepAlive.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutKeepAlive::new);
    }

    public PacketPlayOutKeepAlive(ServerBuffer buf) {
        keepAliveId = buf.readLong();
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
                .writeLong(keepAliveId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutKeepAlive(new FriendlyByteBuf(serialize()));
    }

}
