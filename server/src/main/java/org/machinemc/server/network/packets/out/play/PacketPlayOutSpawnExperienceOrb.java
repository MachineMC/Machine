package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.math.Vector3;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSpawnExperienceOrb extends PacketOut {

    private static final int ID = 0x01;

    private int entityId;
    private Vector3 position;
    private short count;

    static {
        register(PacketPlayOutSpawnExperienceOrb.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnExperienceOrb::new);
    }

    public PacketPlayOutSpawnExperienceOrb(final ServerBuffer buf) {
        entityId = buf.readVarInt();
        position = Vector3.of(buf.readDouble(), buf.readDouble(), buf.readDouble());
        count = buf.readByte();
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
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeDouble(position.getX())
                .writeDouble(position.getY())
                .writeDouble(position.getZ());
        return buf.writeShort(count)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSpawnExperienceOrb(new FriendlyByteBuf(serialize()));
    }

}
