package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.Location;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutWorldSpawnPosition extends PacketOut {

    private static final int ID = 0x4D;

    private BlockPosition position;
    private float angle;

    static {
        register(PacketPlayOutWorldSpawnPosition.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutWorldSpawnPosition::new);
    }

    public PacketPlayOutWorldSpawnPosition(Location location) {
        this(new BlockPosition(location), location.getYaw());
    }

    public PacketPlayOutWorldSpawnPosition(ServerBuffer buf) {
        position = buf.readBlockPos();
        angle = buf.readFloat();
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
                .writeBlockPos(position)
                .writeFloat(angle)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutWorldSpawnPosition(new FriendlyByteBuf(serialize()));
    }
}
