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

import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.chat.ChatSession;
import org.machinemc.api.entities.Player;
import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.entities.player.PlayerTextures;
import org.machinemc.api.utils.FriendlyByteBuf;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.scriptive.components.Component;
import org.machinemc.server.network.packets.PacketOut;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

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
        actions = Action.unpack(buf.readBitSet(Action.BITSET_SIZE));
        final int playerAmount = buf.readVarInt();
        playerInfoDataArray = new PlayerInfoData[playerAmount];
        for (int i = 0; i < playerAmount; i++) {
            final UUID uuid = buf.readUUID();
            final PlayerInfoData data = new PlayerInfoData(uuid);
            for (final Action action : actions)
                action.read(buf, data);
            playerInfoDataArray[i] = data;
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
        buf.writeBitSet(Action.pack(actions), Action.BITSET_SIZE)
                .writeArray(playerInfoDataArray, (buffer, data) -> data.write(actions, buf));
        return buf.bytes();
    }

    @Override
    public PacketOut clone() {
        return new PacketPlayOutPlayerInfo(new FriendlyByteBuf(serialize()));
    }

    @RequiredArgsConstructor
    public enum Action {

        ADD_PLAYER((buf, data) -> buf.writeString(data.getName(), StandardCharsets.UTF_8)
                .writeTextures(data.getPlayerTextures()), (buf, data) -> {
            data.setName(buf.readString(StandardCharsets.UTF_8));
            data.setPlayerTextures(buf.readTextures().orElse(null));
        }),

        INITIALIZE_CHAT((buf, data) -> {
            if (data.getSessionID() == null || data.getPublicKeyData() == null) {
                buf.writeBoolean(false);
                return;
            }
            buf.writeBoolean(true)
                    .writeUUID(data.getSessionID())
                    .writePublicKey(data.getPublicKeyData());
        }, (buf, data) -> {
            if (!buf.readBoolean())
                return;
            data.setSessionID(buf.readUUID());
            data.setPublicKeyData(buf.readPublicKey());
        }),

        UPDATE_GAMEMODE((buf, data) -> buf.writeVarInt(Objects.requireNonNull(data.getGamemode(), "Gamemode can not be null").getID()),
                (buf, data) -> data.setGamemode(Gamemode.fromID(buf.readVarInt()))),

        UPDATE_LISTED((buf, data) -> buf.writeBoolean(data.isListed()),
                (buf, data) -> data.setListed(buf.readBoolean())),

        UPDATE_LATENCY((buf, data) -> buf.writeVarInt(data.getLatency()),
                (buf, data) -> data.setLatency(buf.readVarInt())),

        UPDATE_DISPLAY_NAME((buf, data) -> buf.writeOptional(data.getDisplayName(), ServerBuffer::writeComponent),
                (buf, data) -> data.setDisplayName(buf.readOptional(ServerBuffer::readComponent).orElse(null)));

        static final int BITSET_SIZE = values().length;

        private final BiConsumer<ServerBuffer, PlayerInfoData> writer, reader;

        /**
         * Reads the data from the buffer and modifies the given {@link PlayerInfoData} accordingly.
         * @param buf the server buffer to read from
         * @param data the PlayerInfoData to modify
         */
        public void read(final ServerBuffer buf, final PlayerInfoData data) {
            reader.accept(buf, data);
        }

        /**
         * Reads the data from the {@link PlayerInfoData} and writes it to the given buffer.
         * @param buf the server buffer to write to
         * @param data the PlayerInfoData to read from
         */
        public void write(final ServerBuffer buf, final PlayerInfoData data) {
            writer.accept(buf, data);
        }

        /**
         * Returns the actions of the bitset.
         *
         * @param bitSet bitset
         * @return actions
         */
        public static EnumSet<Action> unpack(final BitSet bitSet) {
            final EnumSet<Action> set = EnumSet.noneOf(Action.class);
            for (final Action action : values()) {
                if (bitSet.get(action.ordinal()))
                    set.add(action);
            }
            return set;
        }

        /**
         * Returns the bitset of a set of actions.
         *
         * @param actions actions
         * @return bitset
         */
        public static BitSet pack(final EnumSet<Action> actions) {
            final BitSet bitSet = new BitSet(BITSET_SIZE);
            for (final Action action : actions)
                bitSet.set(action.ordinal());
            return bitSet;
        }

    }

    /**
     * Player info packet data.
     */
    @Data
    @AllArgsConstructor
    public static final class PlayerInfoData {

        private UUID uuid;
        private @Nullable String name;
        private @Nullable PlayerTextures playerTextures;
        private @Nullable Gamemode gamemode;
        private boolean listed;
        private int latency;
        private @Nullable Component displayName;
        private @Nullable UUID sessionID;
        private @Nullable PublicKeyData publicKeyData;

        private PlayerInfoData(final UUID uuid) {
            this.uuid = uuid;
        }

        public PlayerInfoData(final Player player) {
            this(player.getUUID(),
                    player.getName(),
                    player.getProfile().getTextures().orElse(null),
                    player.getGamemode(),
                    player.isListed(),
                    player.getLatency(),
                    player.getPlayerListName(),
                    player.getChatSession().map(ChatSession::getUUID).orElse(null),
                    player.getChatSession().map(ChatSession::getData).orElse(null));
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
            for (final Action action : actions)
                action.write(buf, this);
        }

    }

}
