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
public class PacketPlayOutEntityVelocity extends PacketOut {

    private static final int ID = 0x52;

    private int entityId;
    private short velocityX, velocityY, velocityZ;

    static {
        register(PacketPlayOutEntityVelocity.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutEntityVelocity::new);
    }

    public PacketPlayOutEntityVelocity(ServerBuffer buf) {
        entityId = buf.readVarInt();
        velocityX = buf.readShort();
        velocityY = buf.readShort();
        velocityZ = buf.readShort();
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
                .writeShort(velocityX)
                .writeShort(velocityY)
                .writeShort(velocityZ)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutEntityVelocity(new FriendlyByteBuf(serialize()));
    }

}
