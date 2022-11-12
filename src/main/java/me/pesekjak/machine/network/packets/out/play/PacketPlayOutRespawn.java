package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.entities.player.Gamemode;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.BlockPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutRespawn extends PacketOut {

    private static final int ID = 0x3E;

    @NotNull
    private NamespacedKey worldType, worldName;
    private long hashedSeed;
    @NotNull
    private Gamemode gamemode;
    @Nullable
    private Gamemode previousGamemode;
    private boolean isDebug, isFlat, copyMetadata, hasDeathLocation;
    @Nullable
    private NamespacedKey deathWorldName;
    @Nullable
    private BlockPosition deathLocation;

    static {
        register(PacketPlayOutRespawn.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutRespawn::new);
    }

    public PacketPlayOutRespawn(FriendlyByteBuf buf) {
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
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
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
