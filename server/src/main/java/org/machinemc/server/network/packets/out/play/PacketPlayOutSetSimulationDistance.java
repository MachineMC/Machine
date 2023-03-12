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
public class PacketPlayOutSetSimulationDistance extends PacketOut {

    private static final int ID = 0x5A;

    @Getter @Setter
    private int distance;

    static {
        register(PacketPlayOutSetSimulationDistance.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSetSimulationDistance::new);
    }

    public PacketPlayOutSetSimulationDistance(ServerBuffer buf) {
        distance = buf.readVarInt();
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
                .writeVarInt(distance)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSetSimulationDistance(new FriendlyByteBuf(serialize()));
    }

}
