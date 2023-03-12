package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.math.Vector2;
import org.machinemc.api.utils.math.Vector3;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.util.UUID;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutSpawnPlayer extends PacketOut {

    private static final int ID = 0x02;

    private int entityId;
    private UUID uuid;
    private Vector3 position;
    private Vector2 rotation;


    static {
        register(PacketPlayOutSpawnPlayer.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutSpawnPlayer::new);
    }

    public PacketPlayOutSpawnPlayer(ServerBuffer buf) {
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
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeVarInt(entityId)
                .writeUUID(uuid)
                .writeDouble(position.getX()).writeDouble(position.getY()).writeDouble(position.getZ())
                .writeAngle((float) rotation.getX()).writeAngle((float) rotation.getY())
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutSpawnPlayer(new FriendlyByteBuf(serialize()));
    }

}
