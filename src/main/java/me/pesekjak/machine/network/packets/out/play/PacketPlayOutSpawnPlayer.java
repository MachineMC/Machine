package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSpawnPlayer extends PacketOut {

    private static final int ID = 0x02;

    private int entityId;
    @NotNull
    private UUID uuid;
    @NotNull
    private Location location;


    static {
        register(PacketPlayOutSpawnPlayer.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnPlayer::new);
    }

    public PacketPlayOutSpawnPlayer(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
        uuid = buf.readUUID();
        location = Location.of(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readAngle(), buf.readAngle(), null);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeUUID(uuid)
                .write(location)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSpawnPlayer(new FriendlyByteBuf(serialize()));
    }

}
