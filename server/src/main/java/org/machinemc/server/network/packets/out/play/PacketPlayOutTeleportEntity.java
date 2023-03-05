package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.math.Vector2;
import org.machinemc.api.utils.math.Vector3;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutTeleportEntity extends PacketOut {

    private static final int ID = 0x66;

    private int entityId;
    private @NotNull Vector3 position;
    private @NotNull Vector2 rotation;
    private boolean onGround;

    static {
        register(PacketPlayOutTeleportEntity.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutTeleportEntity::new);
    }

    public PacketPlayOutTeleportEntity(@NotNull ServerBuffer buf) {
        entityId = buf.readVarInt();
        position = Vector3.of(buf.readDouble(), buf.readDouble(), buf.readDouble());
        rotation = Vector2.of(buf.readAngle(), buf.readAngle());
        onGround = buf.readBoolean();
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
                .writeDouble(position.getX()).writeDouble(position.getY()).writeDouble(position.getZ())
                .writeAngle((float) rotation.getX()).writeAngle((float) rotation.getY())
                .writeBoolean(onGround)
                .bytes();
    }

    @Override
    public @NotNull PacketOut clone() {
        return new PacketPlayOutTeleportEntity(new FriendlyByteBuf(serialize()));
    }

}
