package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.api.world.Location;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutWorldSpawnPosition extends PacketOut {

    private static final int ID = 0x4D;

    private @NotNull BlockPosition position;
    private float angle;

    static {
        register(PacketPlayOutWorldSpawnPosition.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutWorldSpawnPosition::new);
    }

    public PacketPlayOutWorldSpawnPosition(Location location) {
        this(new BlockPosition(location), location.getYaw());
    }

    public PacketPlayOutWorldSpawnPosition(@NotNull ServerBuffer buf) {
        position = buf.readBlockPos();
        angle = buf.readFloat();
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public @NotNull PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte @NotNull [] serialize() {
        return new FriendlyByteBuf()
                .writeBlockPos(position)
                .writeFloat(angle)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutWorldSpawnPosition(new FriendlyByteBuf(serialize()));
    }
}
