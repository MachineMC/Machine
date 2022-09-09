package me.pesekjak.machine.network.packets.out;

import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.entities.Gamemode;
import me.pesekjak.machine.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PacketPlayLogin extends PacketOut {

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
    private final byte previousGamemode = -1; // TODO implement later
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
        PacketOut.register(PacketPlayLogin.class, ID, PacketState.PLAY_OUT,
                PacketPlayLogin::new
        );
    }

    public PacketPlayLogin(FriendlyByteBuf buf) {
        entityID = buf.readInt();
        isHardcore = buf.readBoolean();
        gamemode = Gamemode.fromID(buf.readByte());
        buf.readByte(); // reading previous gamemode
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
                .writeByte((byte) gamemode.getID())
                .writeByte(previousGamemode)
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
        return new PacketPlayLogin(new FriendlyByteBuf(serialize()));
    }

}
