package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.Location;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutWorldSpawnPosition extends PacketOut {

    private static final int ID = 0x4D;

    @NotNull
    private BlockPosition position;
    private float angle;

    static {
        register(PacketPlayOutWorldSpawnPosition.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutWorldSpawnPosition::new);
    }

    public PacketPlayOutWorldSpawnPosition(Location location) {
        this(new BlockPosition(location), location.getYaw());
    }

    public PacketPlayOutWorldSpawnPosition(FriendlyByteBuf buf) {
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
