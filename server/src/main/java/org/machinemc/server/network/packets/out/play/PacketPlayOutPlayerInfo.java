package org.machinemc.server.network.packets.out.play;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.machinemc.scriptive.components.Component;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.entities.Player;
import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.server.network.packets.PacketOut;
import org.machinemc.server.utils.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ToString
@Getter @Setter
public class PacketPlayOutPlayerInfo extends PacketOut {

    private static final int ID = 0x37;

    private Action action;
    private PlayerInfoData[] playerInfoDataArray;

    static {
        register(PacketPlayOutPlayerInfo.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPlayerInfo::new);
    }

    public PacketPlayOutPlayerInfo(final Action action, final PlayerInfoData... playerInfoDataArray) {
        this.action = action;
        this.playerInfoDataArray = playerInfoDataArray;
    }

    public PacketPlayOutPlayerInfo(final Action action, final Player... players) {
        this.action = action;
        playerInfoDataArray = new PlayerInfoData[players.length];
        for (int i = 0; i < players.length; i++)
            playerInfoDataArray[i] = new PlayerInfoData(players[i]);
    }

    public PacketPlayOutPlayerInfo(final ServerBuffer buf) {
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
                case REMOVE_PLAYER -> {
                }
            }
            playerInfoDataArray[i] = new PlayerInfoData(uuid, name, skin, gamemode,
                    latency, displayName, publicKeyData);
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

        /**
         * @return id of the action
         */
        public int getId() {
            return ordinal();
        }

        /**
         * Returns action with a given id.
         *
         * @param id id
         * @return action
         */
        public static Action fromID(final @Range(from = 0, to = 4) int id) {
            Preconditions.checkArgument(id < values().length, "Unsupported Action type");
            return values()[id];
        }

    }

    /**
     * Player info packet data.
     *
     * @param uuid           uuid of the player
     * @param name           name of the player
     * @param playerTextures textures of the player
     * @param gamemode       gamemode of the player
     * @param latency        latency of the player
     * @param listName       name displayed in player list
     * @param publicKeyData  public key data
     */
    public record PlayerInfoData(UUID uuid,
                                 @Nullable String name,
                                 @Nullable PlayerTextures playerTextures,
                                 @Nullable Gamemode gamemode,
                                 int latency,
                                 @Nullable Component listName,
                                 @Nullable PublicKeyData publicKeyData) {

        public PlayerInfoData(final Player player) {
            this(player.getUuid(),
                    player.getName(),
                    player.getProfile().getTextures(),
                    player.getGamemode(),
                    player.getLatency(),
                    player.getPlayerListName(),
                    player.getServer().isOnline() ? player.getConnection().getPublicKeyData() : null);
        }

        /**
         * Writes the data to the buf for the packet depending on
         * the provided action.
         *
         * @param action action
         * @param buf    buffer to write into
         */
        public void write(final Action action, final FriendlyByteBuf buf) {
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
                case REMOVE_PLAYER -> {
                }
            }
        }
    }
}
