package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.Location;

@AllArgsConstructor
public class PacketPlayOutWorldSpawnPosition extends PacketOut {

    private static final int ID = 0x4D;

    @Getter @Setter
    private BlockPosition position;
    @Getter @Setter
    private byte angle;

    static {
        PacketOut.register(PacketPlayOutWorldSpawnPosition.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutWorldSpawnPosition::new);
    }

    public PacketPlayOutWorldSpawnPosition(Location location) {
        this(new BlockPosition(location), location.getYawAsByte());
    }

    public PacketPlayOutWorldSpawnPosition(FriendlyByteBuf buf) {
        position = buf.readBlockPos();
        angle = buf.readByte();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeBlockPos(position)
                .writeByte(angle)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutWorldSpawnPosition(new FriendlyByteBuf(serialize()));
    }
}
