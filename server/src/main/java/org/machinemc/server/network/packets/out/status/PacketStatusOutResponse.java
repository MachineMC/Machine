package org.machinemc.server.network.packets.out.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@ToString
public class PacketStatusOutResponse extends PacketOut {

    private static final int ID = 0x00;

    static {
        register(PacketStatusOutResponse.class, ID, PacketState.STATUS_OUT,
                PacketStatusOutResponse::new
        );
    }

    @Getter @Setter
    private String json;

    public PacketStatusOutResponse(ServerBuffer buf) {
        json = buf.readString(StandardCharsets.UTF_8);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.STATUS_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeString(json, StandardCharsets.UTF_8)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketStatusOutResponse(new FriendlyByteBuf(serialize()));
    }

}
