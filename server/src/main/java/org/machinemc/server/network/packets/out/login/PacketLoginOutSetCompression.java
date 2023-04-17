package org.machinemc.server.network.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;

@AllArgsConstructor
@ToString
public class PacketLoginOutSetCompression extends PacketOut {

    private static final int ID = 0x03;

    @Getter @Setter
    private int threshold;

    static {
        register(PacketLoginOutSetCompression.class, ID, PacketState.LOGIN_OUT,
                PacketLoginOutSetCompression::new
        );
    }

    public PacketLoginOutSetCompression(final ServerBuffer buf) {
        threshold = buf.readVarInt();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.LOGIN_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(threshold)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketLoginOutSetCompression(new FriendlyByteBuf(serialize()));
    }

}
