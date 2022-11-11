package me.pesekjak.machine.network.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.entities.player.Gamemode;
import me.pesekjak.machine.utils.NamespacedKey;
import me.pesekjak.machine.world.BlockPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@ToString
@Getter @Setter
public class PacketPlayOutLogin extends PacketOut {

    private static final int ID = 0x25;

    public static final NamespacedKey DIMENSION_TYPE_CODEC_NAME = NamespacedKey.minecraft("dimension_type");
    public static final NamespacedKey WORLD_GEN_BIOME_CODEC_NAME = NamespacedKey.minecraft("worldgen/biome");

    private int entityID;
    private boolean isHardcore;
    @NotNull
    private Gamemode gamemode;
    @Nullable
    private Gamemode previousGamemode;
    @NotNull
    private List<String> dimensions;
    @NotNull
    private NBTCompound dimensionCodec;
    @NotNull
    private NamespacedKey spawnWorldType;
    @NotNull
    private NamespacedKey spawnWorld;
    private long hashedSeed;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean isDebug;
    private boolean isFlat;
    private boolean hasDeathLocation;
    @Nullable
    private NamespacedKey deathWorldName;
    @Nullable
    private BlockPosition deathLocation;

    static {
        register(PacketPlayOutLogin.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutLogin::new
        );
    }

    public PacketPlayOutLogin(FriendlyByteBuf buf) {
        entityID = buf.readInt();
        isHardcore = buf.readBoolean();
        gamemode = Gamemode.fromID(buf.readByte());
        byte gamemodeId = buf.readByte();
        previousGamemode = gamemodeId == -1 ? null : Gamemode.fromID(gamemodeId);
        dimensions = buf.readStringList(StandardCharsets.UTF_8);
        dimensionCodec = (NBTCompound) buf.readNBT();
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
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeInt(entityID)
                .writeBoolean(isHardcore)
                .writeByte((byte) gamemode.getId())
                .writeByte((byte) (previousGamemode == null ? -1 : previousGamemode.getId()))
                .writeStringList(new ArrayList<>(dimensions), StandardCharsets.UTF_8)
                .writeNBT("", dimensionCodec)
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
