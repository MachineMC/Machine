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
public class PacketPlayOutHideMessage extends PacketOut {

    private static final int ID = 0x18;

    @Getter @Setter
    private byte[] signature;

    static {
        register(PacketPlayOutHideMessage.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutHideMessage::new);
    }

    public PacketPlayOutHideMessage(final ServerBuffer buf) {
        signature = buf.readByteArray();
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
                .writeByteArray(signature)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutHideMessage(new FriendlyByteBuf(serialize()));
    }

}
