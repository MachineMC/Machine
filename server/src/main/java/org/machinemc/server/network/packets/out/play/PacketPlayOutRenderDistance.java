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
public class PacketPlayOutRenderDistance extends PacketOut {

    private static final int ID = 0x4C;

    @Getter @Setter
    private int viewDistance; // (2-32)

    static {
        register(PacketPlayOutRenderDistance.class, ID, Packet.PacketState.PLAY_OUT,
                PacketPlayOutRenderDistance::new);
    }

    public PacketPlayOutRenderDistance(ServerBuffer buf) {
        viewDistance = buf.readVarInt();
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
                .writeVarInt(viewDistance)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutRenderDistance(new FriendlyByteBuf(serialize()));
    }

}
