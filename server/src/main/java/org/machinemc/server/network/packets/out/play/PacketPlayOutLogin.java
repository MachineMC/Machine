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
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutLogin extends PacketOut {

    private static final int ID = 0x25;

    private int entityID;
    private boolean isHardcore;
    private Gamemode gamemode;
    private @Nullable Gamemode previousGamemode;
    private List<String> dimensions;
    private NBTCompound dimensionCodec;
    private NamespacedKey spawnWorldType;
    private NamespacedKey spawnWorld;
    private long hashedSeed;
    private int maxPlayers,
            viewDistance,
            simulationDistance;
    private boolean reducedDebugInfo,
            enableRespawnScreen,
            isDebug,
            isFlat,
            hasDeathLocation;
    private @Nullable NamespacedKey deathWorldName;
    private @Nullable BlockPosition deathLocation;

    static {
        register(PacketPlayOutLogin.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutLogin::new
        );
    }

    public PacketPlayOutLogin(final ServerBuffer buf) {
        entityID = buf.readInt();
        isHardcore = buf.readBoolean();
        gamemode = Gamemode.fromID(buf.readByte());
        byte gamemodeId = buf.readByte();
        previousGamemode = gamemodeId == -1 ? null : Gamemode.fromID(gamemodeId);
        dimensions = buf.readStringList(StandardCharsets.UTF_8);
        this.dimensionCodec = buf.readNBT();
        spawnWorldType = buf.readNamespacedKey();
        spawnWorld = buf.readNamespacedKey();
        hashedSeed = buf.readLong();
        maxPlayers = buf.readVarInt();
        viewDistance = buf.readVarInt();
        simulationDistance = buf.readVarInt();
        reducedDebugInfo = buf.readBoolean();
        enableRespawnScreen = buf.readBoolean();
        isDebug = buf.readBoolean();
        isFlat = buf.readBoolean();
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
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeInt(entityID)
                .writeBoolean(isHardcore)
                .writeByte((byte) gamemode.getId())
                .writeByte((byte) (previousGamemode == null ? -1 : previousGamemode.getId()))
                .writeStringList(new ArrayList<>(dimensions), StandardCharsets.UTF_8)
                .writeNBT(dimensionCodec)
                .writeNamespacedKey(spawnWorldType)
                .writeNamespacedKey(spawnWorld)
                .writeLong(hashedSeed)
                .writeVarInt(maxPlayers)
                .writeVarInt(viewDistance)
                .writeVarInt(simulationDistance)
                .writeBoolean(reducedDebugInfo)
                .writeBoolean(enableRespawnScreen)
                .writeBoolean(isDebug)
                .writeBoolean(isFlat)
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
        return new PacketPlayOutLogin(new FriendlyByteBuf(serialize()));
    }

}
