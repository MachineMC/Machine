package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.Location;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutTeleportEntity extends PacketOut {

    private static final int ID = 0x66;

    private int entityId;
    @NotNull
    private Location location;
    private boolean onGround;

    static {
        register(PacketPlayOutTeleportEntity.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutTeleportEntity::new);
    }

    public PacketPlayOutTeleportEntity(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        location = Location.of(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), null);
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
