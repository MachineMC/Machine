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
public class PacketPlayOutSpawnExperienceOrb extends PacketOut {

    private static final int ID = 0x01;

    private int entityId;
    @NotNull
    private Location location;
    private short count;

    static {
        register(PacketPlayOutSpawnExperienceOrb.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnExperienceOrb::new);
    }

    public PacketPlayOutSpawnExperienceOrb(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        location = Location.of(buf.readDouble(), buf.readDouble(), buf.readDouble(), null);
        count = buf.readByte();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeVarInt(entityId);
        location.writePos(buf);
        return buf.writeShort(count)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSpawnExperienceOrb(new FriendlyByteBuf(serialize()));
    }

}
