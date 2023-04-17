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
@Getter @Setter
public class PacketPlayOutHeadRotation extends PacketOut {

    private static final int ID = 0x3F;

    private int entityId;
    private float angle;

    static {
        register(PacketPlayOutHeadRotation.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutHeadRotation::new);
    }

    public PacketPlayOutHeadRotation(final ServerBuffer buf) {
        entityId = buf.readVarInt();
        angle = buf.readAngle();
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
                .writeVarInt(entityId)
                .writeAngle(angle)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutHeadRotation(new FriendlyByteBuf(serialize()));
    }

}
