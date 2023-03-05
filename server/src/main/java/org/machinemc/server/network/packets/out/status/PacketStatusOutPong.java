package org.machinemc.server.network.packets.out.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

@AllArgsConstructor
@ToString
public class PacketStatusOutPong extends PacketOut {

    private static final int ID = 0x01;

    @Getter @Setter
    private long payload;

    static {
        register(PacketStatusOutPong.class, ID, Packet.PacketState.STATUS_OUT,
                PacketStatusOutPong::new
        );
    }

    public PacketStatusOutPong(ServerBuffer buf) {
        payload = buf.readLong();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.STATUS_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeLong(payload)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketStatusOutPong(new FriendlyByteBuf(serialize()));
    }

}
