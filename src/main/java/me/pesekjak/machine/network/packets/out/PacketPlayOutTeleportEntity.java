package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.Location;

@AllArgsConstructor
public class PacketPlayOutTeleportEntity extends PacketOut {

    private static final int ID = 0x66;

    @Getter @Setter
    private int entityId;
    @Getter @Setter
    private Location location;
    @Getter @Setter
    private boolean onGround;

    static {
        register(PacketPlayOutTeleportEntity.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutTeleportEntity::new);
    }

    public PacketPlayOutTeleportEntity(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        location = new Location(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readAngle(), buf.readAngle(), null);
        onGround = buf.readBoolean();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeVarInt(entityId);
        location.write(buf);
        return buf.writeBoolean(onGround)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutTeleportEntity(new FriendlyByteBuf(serialize()));
    }

}
