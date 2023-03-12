package org.machinemc.server.network.packets.in.status;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketIn;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

@AllArgsConstructor
@ToString
public class PacketStatusInRequest extends PacketIn {

    private static final int ID = 0x00;

    static {
        register(PacketStatusInRequest.class, ID, PacketState.STATUS_IN,
                PacketStatusInRequest::new
        );
    }

    public PacketStatusInRequest(ServerBuffer buf) {

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
        return new byte[1];
    }

    @Override
    public PacketIn clone() {
        return new PacketStatusInRequest(new FriendlyByteBuf(serialize()));
    }

}
