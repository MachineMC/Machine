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
public class PacketPlayOutLinkEntities extends PacketOut {

    private static final int ID = 0x51;

    @Getter @Setter
    private int attachedEntityId, holdingEntityId;

    static {
        register(PacketPlayOutLinkEntities.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutLinkEntities::new);
    }

    public PacketPlayOutLinkEntities(ServerBuffer buf) {
        attachedEntityId = buf.readInt();
        holdingEntityId = buf.readInt();
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
                .writeInt(attachedEntityId)
                .writeInt(holdingEntityId)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutLinkEntities(new FriendlyByteBuf(serialize()));
    }

}
