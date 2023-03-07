package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
public class PacketPlayOutCloseContainer extends PacketOut {

    private static final int ID = 0x10;

    @Getter @Setter
    private byte windowId;

    static {
        register(PacketPlayOutCloseContainer.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutCloseContainer::new);
    }

    public PacketPlayOutCloseContainer(ServerBuffer buf) {
        windowId = buf.readByte();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Packet.PacketState getPacketState() {
        return Packet.PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeByte(windowId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutCloseContainer(new FriendlyByteBuf(serialize()));
    }

}
