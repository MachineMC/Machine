package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.Location;

@AllArgsConstructor
public class PacketPlayOutSpawnExperienceOrb extends PacketOut {

    private static final int ID = 0x01;

    @Getter @Setter
    private int entityId;
    @Getter @Setter
    private Location location;
    @Getter @Setter
    private short count;

    static {
        register(PacketPlayOutSpawnExperienceOrb.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnExperienceOrb::new);
    }

    public PacketPlayOutSpawnExperienceOrb(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        location = Location.of(buf.readDouble(), buf.readDouble(), buf.readDouble(), 0, 0, null);
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
