package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.Location;

import java.util.UUID;

@AllArgsConstructor
public class PacketPlayOutSpawnPlayer extends PacketOut {

    private static final int ID = 0x02;

    @Getter @Setter
    private int entityId;
    @Getter @Setter
    private UUID uuid;
    @Getter @Setter
    private Location location;


    static {
        register(PacketPlayOutSpawnPlayer.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnPlayer::new);
    }

    public PacketPlayOutSpawnPlayer(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        uuid = buf.readUUID();
        location = new Location(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readAngle(), buf.readAngle(), null);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeUUID(uuid);
        location.write(buf);
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSpawnPlayer(new FriendlyByteBuf(serialize()));
    }

}
