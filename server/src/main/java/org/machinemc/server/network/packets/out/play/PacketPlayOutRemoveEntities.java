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
public class PacketPlayOutRemoveEntities extends PacketOut {

    private static final int ID = 0x3B;

    @Getter @Setter
    private int[] entityIds;

    static {
        register(PacketPlayOutRemoveEntities.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutRemoveEntities::new);
    }

    public PacketPlayOutRemoveEntities(ServerBuffer buf) {
        entityIds = buf.readVarIntArray();
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
                .writeVarIntArray(entityIds)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutRemoveEntities(new FriendlyByteBuf(serialize()));
    }

}