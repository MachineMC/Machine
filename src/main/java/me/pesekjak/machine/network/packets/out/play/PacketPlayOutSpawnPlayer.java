package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import me.pesekjak.machine.utils.math.Vector2;
import me.pesekjak.machine.utils.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSpawnPlayer extends PacketOut {

    private static final int ID = 0x02;

    private int entityId;
    private @NotNull UUID uuid;
    private @NotNull Vector3 position;
    private @NotNull Vector2 rotation;


    static {
        register(PacketPlayOutSpawnPlayer.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnPlayer::new);
    }

    public PacketPlayOutSpawnPlayer(@NotNull ServerBuffer buf) {
        entityId = buf.readVarInt();
        uuid = buf.readUUID();
        position = Vector3.of(buf.readDouble(), buf.readDouble(), buf.readDouble());
        rotation = Vector2.of(buf.readAngle(), buf.readAngle());
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
                .writeVarInt(entityId)
                .writeUUID(uuid)
                .writeDouble(position.getX()).writeDouble(position.getY()).writeDouble(position.getZ())
                .writeAngle((float) rotation.getX()).writeAngle((float) rotation.getY())
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutSpawnPlayer(new FriendlyByteBuf(serialize()));
    }

}
