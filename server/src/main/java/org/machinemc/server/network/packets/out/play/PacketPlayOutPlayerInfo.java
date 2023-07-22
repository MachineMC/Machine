/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.network.packets.out.play;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.entities.Player;
import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.network.packets.PacketOut;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PacketPlayOutPlayerInfo extends PacketOut {

    private static final int ID = 0x3A;

    private EnumSet<Action> actions;
    private PlayerInfoData[] playerInfoDataArray;

    static {
        register(PacketPlayOutPlayerInfo.class, ID, PacketState.PLAY_OUT,
                PacketPlayOutPlayerInfo::new);
    }

    public PacketPlayOutPlayerInfo(final EnumSet<Action> actions, final PlayerInfoData... playerInfoDataArray) {
        this.actions = actions;
        this.playerInfoDataArray = playerInfoDataArray;
    }

    public PacketPlayOutPlayerInfo(final EnumSet<Action> actions, final Player... players) {
        this.actions = actions;
        playerInfoDataArray = new PlayerInfoData[players.length];
        for (int i = 0; i < players.length; i++)
            playerInfoDataArray[i] = new PlayerInfoData(players[i]);
    }

    public PacketPlayOutPlayerInfo(final ServerBuffer buf) {
        actions = Action.unpack(buf.readByte());
        final int playerAmount = buf.readVarInt();
        playerInfoDataArray = new PlayerInfoData[playerAmount];
        for (int i = 0; i < playerAmount; i++) {
            final UUID uuid = buf.readUUID();
            String name = null;
            PlayerTextures skin = null;
            Gamemode gamemode = null;
            boolean listed = false;
            int latency = 0;
            Component displayName = null;
            UUID sessionID = null;
            PublicKeyData publicKeyData = null;
            for (final Action action : actions) {
                switch (action) {
                    case ADD_PLAYER -> {
                        name = buf.readString(StandardCharsets.UTF_8);
                        skin = buf.readTextures().orElse(null);
                    }
                    case INITIALIZE_CHAT -> {
                        if (!buf.readBoolean())
                            continue;
                        sessionID = buf.readUUID();
                        publicKeyData = buf.readPublicKey();
                    }
                    case UPDATE_GAMEMODE -> gamemode = Gamemode.fromID(buf.readVarInt());
                    case UPDATE_LISTED -> listed = buf.readBoolean();
                    case UPDATE_LATENCY -> latency = buf.readVarInt();
                    case UPDATE_DISPLAY_NAME -> displayName = buf.readComponent();
                }
                playerInfoDataArray[i] = new PlayerInfoData(uuid, name, skin, gamemode, listed,
                        latency, displayName, sessionID, publicKeyData);
            }
        }
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public PacketState getPacketState() {
        return PacketState.PLAY_OUT;
    }

    @Override
    public byte[] serialize() {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeByte(Action.pack(actions))
                .writeVarInt(playerInfoDataArray.length);
        for (final PlayerInfoData playerInfoData : playerInfoDataArray)
            playerInfoData.write(actions, buf);
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPlayerInfo(new FriendlyByteBuf(serialize()));
    }

    public enum Action {
        ADD_PLAYER,
        INITIALIZE_CHAT,
        UPDATE_GAMEMODE,
        UPDATE_LISTED,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME;

        /**
         * Returns the actions of the bit mask.
         *
         * @param mask bit mask
         * @return actions
         */
        public static EnumSet<Action> unpack(final byte mask) {
            final EnumSet<Action> set = EnumSet.noneOf(Action.class);
            for (final Action action : values()) {
                if (((mask >> action.ordinal()) & 1) == 1)
                    set.add(action);
            }
            return set;
        }

        /**
         * Returns the bit mask of a set of actions.
         *
         * @param actions actions
         * @return bit mask
         */
        public static byte pack(final EnumSet<Action> actions) {
            byte mask = 0;
            for (final Action action : actions)
                mask |= 1 << action.ordinal();
            return mask;
        }

    }

    /**
     * Player info packet data.
     *
     * @param uuid           uuid of the player
     * @param name           name of the player
     * @param playerTextures textures of the player
     * @param gamemode       gamemode of the player
     * @param listed         whether the player should be listed in the player list
     * @param latency        latency of the player
     * @param listName       name displayed in player list
     * @param sessionID      session id of the connection
     * @param publicKeyData  public key data of the connection
     */
    public record PlayerInfoData(UUID uuid,
                                 @Nullable String name,
                                 @Nullable PlayerTextures playerTextures,
                                 @Nullable Gamemode gamemode,
                                 boolean listed,
                                 int latency,
                                 @Nullable Component listName,
                                 @Nullable UUID sessionID,
                                 @Nullable PublicKeyData publicKeyData) {

        public PlayerInfoData(final Player player) {
            this(player.getUUID(),
                    player.getName(),
                    player.getProfile().getTextures().orElse(null),
                    player.getGamemode(),
                    player.isListed(),
                    player.getLatency(),
                    player.getPlayerListName(),
                    player.getServer().isOnline() ? player.getConnection().getSessionId().orThrow() : null,
                    player.getServer().isOnline() ? player.getConnection().getPublicKeyData().orThrow() : null);
        }

        /**
         * Writes the data to the buf for the packet depending on
         * the provided actions.
         *
         * @param actions actions
         * @param buf    buffer to write into
         */
        public void write(final EnumSet<Action> actions, final FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            for (final Action action : actions) {
                switch (action) {
                    case ADD_PLAYER -> {
                        assert name != null;
                        buf.writeString(name, StandardCharsets.UTF_8)
                                .writeTextures(playerTextures);
                    }
                    case INITIALIZE_CHAT -> {
                        if (sessionID == null || publicKeyData == null) {
                            buf.writeBoolean(false);
                            continue;
                        }
                        buf.writeBoolean(true)
                                .writeUUID(sessionID)
                                .writePublicKey(publicKeyData);
                    }
                    case UPDATE_GAMEMODE -> {
                        assert gamemode != null;
                        buf.writeVarInt(gamemode.getId());
                    }
                    case UPDATE_LISTED -> buf.writeBoolean(listed);
                    case UPDATE_LATENCY -> buf.writeVarInt(latency);
                    case UPDATE_DISPLAY_NAME -> {
                        assert listName != null;
                        buf.writeComponent(listName)
                    }
                }
            }
        }
    }
}
