package me.pesekjak.machine.network.packets.out;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.pesekjak.machine.auth.PublicKeyData;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.entities.player.Gamemode;
import me.pesekjak.machine.entities.player.PlayerTextures;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ToString
@Getter @Setter
public class PacketPlayOutPlayerInfo extends PacketOut {

    private static final int ID = 0x37;

    @NotNull
    private Action action;
    @NotNull
    private PlayerInfoData[] playerInfoDataArray;

    static {
        register(PacketPlayOutPlayerInfo.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPlayerInfo::new);
    }

    public PacketPlayOutPlayerInfo(Action action, PlayerInfoData... playerInfoDataArray) {
        this.action = action;
        this.playerInfoDataArray = playerInfoDataArray;
    }

    public PacketPlayOutPlayerInfo(Action action, Player... players) {
        this.action = action;
        playerInfoDataArray = new PlayerInfoData[players.length];
        for (int i = 0; i < players.length; i++)
            playerInfoDataArray[i] = new PlayerInfoData(players[i]);
    }

    public PacketPlayOutPlayerInfo(FriendlyByteBuf buf) {
        action = Action.fromID(buf.readVarInt());
        int playerAmount = buf.readVarInt();
        playerInfoDataArray = new PlayerInfoData[playerAmount];
        for (int i = 0; i < playerAmount; i++) {
            UUID uuid = buf.readUUID();
            String name = null;
            PlayerTextures skin = null;
            Gamemode gamemode = null;
            int latency = 0;
            Component displayName = null;
            PublicKeyData publicKeyData = null;
            switch (action) {
                case ADD_PLAYER -> {
                    name = buf.readString(StandardCharsets.UTF_8);
                    skin = buf.readTextures();
                    gamemode = Gamemode.fromID(buf.readVarInt());
                    latency = buf.readVarInt();
                    if (buf.readBoolean())
                        displayName = buf.readComponent();
                    if (buf.readBoolean()) {
                        publicKeyData = buf.readPublicKey();
                    }
                }
                case UPDATE_GAMEMODE -> gamemode = Gamemode.fromID(buf.readVarInt());
                case UPDATE_LATENCY -> latency = buf.readVarInt();
                case UPDATE_DISPLAY_NAME -> displayName = buf.readComponent();
                case REMOVE_PLAYER -> {}
            }
            playerInfoDataArray[i] = new PlayerInfoData(uuid, name, skin, gamemode, latency, displayName, publicKeyData);
        }
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public byte[] serialize() {
        FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeVarInt(action.getId())
                .writeVarInt(playerInfoDataArray.length);
        for (PlayerInfoData playerInfoData : playerInfoDataArray)
            playerInfoData.write(action, buf);
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPlayerInfo(new FriendlyByteBuf(serialize()));
    }

    public enum Action {
        ADD_PLAYER,
        UPDATE_GAMEMODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;

        public int getId() {
            return ordinal();
        }

        public static @NotNull Action fromID(@Range(from = 0, to = 4) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Action type");
            return values()[id];
        }

    }

    public record PlayerInfoData(UUID uuid, @Nullable String name, @Nullable PlayerTextures playerTextures, @Nullable Gamemode gamemode, int latency,
                                 @Nullable Component listName, @Nullable PublicKeyData publicKeyData) {

        public PlayerInfoData(Player player) {
            this(player.getUuid(), player.getName(), player.getProfile().getTextures(), player.getGamemode(), player.getLatency(),
                    player.getPlayerListName(), player.getServer().isOnline() ? player.getConnection().getPublicKeyData() : null);
        }

        public void write(Action action, FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            switch (action) {
                case ADD_PLAYER -> {
                    assert name != null;
                    assert gamemode != null;
                    buf.writeString(name, StandardCharsets.UTF_8)
                            .writeTextures(playerTextures)
                            .writeVarInt(gamemode.getId())
                            .writeVarInt(latency)
                            .writeBoolean(listName != null);
                    if (listName != null)
                        buf.writeComponent(listName);
                    buf.writeBoolean(publicKeyData != null);
                    if (publicKeyData != null)
                        buf.writePublicKey(publicKeyData);
                }
                case UPDATE_GAMEMODE -> {
                    assert gamemode != null;
                    buf.writeVarInt(gamemode.getId());
                }
                case UPDATE_LATENCY -> buf.writeVarInt(latency);
                case UPDATE_DISPLAY_NAME -> {
                    buf.writeBoolean(listName != null);
                    if (listName != null)
                        buf.writeComponent(listName);
                }
                case REMOVE_PLAYER -> {}
            }
        }
    }
}