package org.machinemc.server.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.world.BlockPosition;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutRespawn extends PacketOut {

    private static final int ID = 0x3E;

    private NamespacedKey worldType, worldName;
    private long hashedSeed;
    private Gamemode gamemode;
    private @Nullable Gamemode previousGamemode;
    private boolean isDebug, isFlat, copyMetadata, hasDeathLocation;
    private @Nullable NamespacedKey deathWorldName;
    private @Nullable BlockPosition deathLocation;

    static {
        register(PacketPlayOutRespawn.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutRespawn::new);
    }

    public PacketPlayOutRespawn(final ServerBuffer buf) {
        worldType = buf.readNamespacedKey();
        worldName = buf.readNamespacedKey();
        hashedSeed = buf.readLong();
        gamemode = Gamemode.fromID(buf.readByte());
        previousGamemode = Gamemode.nullableFromID(buf.readByte());
        isDebug = buf.readBoolean();
        isFlat = buf.readBoolean();
        copyMetadata = buf.readBoolean();
        hasDeathLocation = buf.readBoolean();
        if (hasDeathLocation) {
            deathWorldName = buf.readNamespacedKey();
            deathLocation = buf.readBlockPos();
        }
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
        final FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeNamespacedKey(worldType)
                .writeNamespacedKey(worldName)
                .writeLong(hashedSeed)
                .writeByte((byte) gamemode.getId())
                .writeByte((byte) (previousGamemode == null ? -1 : previousGamemode.getId()))
                .writeBoolean(isDebug)
                .writeBoolean(isFlat)
                .writeBoolean(copyMetadata)
                .writeBoolean(hasDeathLocation);
        if (hasDeathLocation) {
            assert deathWorldName != null;
            assert deathLocation != null;
            buf.writeNamespacedKey(deathWorldName)
                    .writeBlockPos(deathLocation);
        }
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutRespawn(new FriendlyByteBuf(serialize()));
    }

}
