package org.machinemc.server.network.packets.in.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

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

    public PacketStatusInPing(final ServerBuffer buf) {
        payload = buf.readLong();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.STATUS_IN;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeLong(payload)
                .bytes();
    }

    @Override
    public PacketIn clone() {
        return new PacketStatusInPing(new FriendlyByteBuf(serialize()));
    }

}
