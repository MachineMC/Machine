package me.pesekjak.machine.network.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.entities.player.Gamemode;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PacketPlayOutLogin extends PacketOut {

    public static int ID = 0x25;

    public static final NamespacedKey DIMENSION_TYPE_CODEC_NAME = NamespacedKey.minecraft("dimension_type");
    public static final NamespacedKey WORLD_GEN_BIOME_CODEC_NAME = NamespacedKey.minecraft("worldgen/biome");

    @Getter @Setter
    private int entityID;
    @Getter @Setter
    private boolean isHardcore;
    @Getter @Setter @NotNull
    private Gamemode gamemode;
    @SuppressWarnings("FieldCanBeLocal")
    private Gamemode previousGamemode;
    @Getter @Setter
    private List<String> dimensions;
    @Getter @Setter
    private NBTCompound dimensionCodec;
    @Getter @Setter
    private String spawnWorldType;
    @Getter @Setter
    private String spawnWorld;
    @Getter @Setter
    private long hashedSeed;
    @Getter @Setter
    private int maxPlayers;
    @Getter @Setter
    private int viewDistance;
    @Getter @Setter
    private int simulationDistance;
    @Getter @Setter
    private boolean reducedDebugInfo;
    @Getter @Setter
    private boolean enableRespawnScreen;
    @Getter @Setter
    private boolean isDebug;
    @Getter @Setter
    private boolean isFlat;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean hasDeathLocation = false; // TODO implement later

    static {
        PacketOut.register(PacketPlayOutLogin.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutLogin::new
        );
    }

    public PacketPlayOutLogin(FriendlyByteBuf buf) {
        entityID = buf.readInt();
        isHardcore = buf.readBoolean();
        gamemode = Gamemode.fromID(buf.readByte());
        byte gamemodeId = buf.readByte();
        previousGamemode = gamemodeId == -1 ? null : Gamemode.fromID(gamemodeId); // reading previous gamemode
        dimensions = buf.readStringList(StandardCharsets.UTF_8);
        dimensionCodec = (NBTCompound) buf.readNBT();
        spawnWorldType = buf.readString(StandardCharsets.UTF_8);
        spawnWorld = buf.readString(StandardCharsets.UTF_8);
        hashedSeed = buf.readLong();
        maxPlayers = buf.readVarInt();
        viewDistance = buf.readVarInt();
        simulationDistance = buf.readVarInt();
        reducedDebugInfo = buf.readBoolean();
        enableRespawnScreen = buf.readBoolean();
        isDebug = buf.readBoolean();
        isFlat = buf.readBoolean();
        buf.readBoolean(); // reading if has death location
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        return new FriendlyByteBuf()
                .writeInt(entityID)
                .writeBoolean(isHardcore)
                .writeByte((byte) gamemode.getId())
                .writeByte((byte) (previousGamemode == null ? -1 : previousGamemode.getId()))
                .writeStringList(new ArrayList<>(dimensions), StandardCharsets.UTF_8)
                .writeNBT("", dimensionCodec)
                .writeString(spawnWorldType, StandardCharsets.UTF_8)
                .writeString(spawnWorld, StandardCharsets.UTF_8)
                .writeLong(hashedSeed)
                .writeVarInt(maxPlayers)
                .writeVarInt(viewDistance)
                .writeVarInt(simulationDistance)
                .writeBoolean(reducedDebugInfo)
                .writeBoolean(enableRespawnScreen)
                .writeBoolean(isDebug)
                .writeBoolean(isFlat)
                .writeBoolean(hasDeathLocation)
                .bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutLogin(new FriendlyByteBuf(serialize()));
    }

}
