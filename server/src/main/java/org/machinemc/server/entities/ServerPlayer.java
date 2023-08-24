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
package org.machinemc.server.entities;

import com.google.common.hash.Hashing;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.Server;
import org.machinemc.api.chat.ChatMode;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.entities.EntityType;
import org.machinemc.api.entities.Player;
import org.machinemc.api.entities.player.Gamemode;
import org.machinemc.api.entities.player.Hand;
import org.machinemc.api.entities.player.PlayerProfile;
import org.machinemc.api.entities.player.SkinPart;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.server.PlayerManager;
import org.machinemc.api.server.codec.Codec;
import org.machinemc.api.world.*;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.out.play.*;
import org.machinemc.server.network.packets.out.play.PacketPlayOutSynchronizePlayerPosition.TeleportFlags;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of player.
 */
public final class ServerPlayer extends ServerLivingEntity implements Player {

    @Getter
    private final ClientConnection connection;
    @Getter @Setter
    private PlayerProfile profile;

    @Getter
    private Gamemode gamemode = Gamemode.CREATIVE; // for now
    @Getter(AccessLevel.NONE)
    private @Nullable Gamemode previousGamemode = null;

    @Getter @Setter
    private String locale;
    @Getter @Setter
    private byte viewDistance;
    @Getter @Setter
    private ChatMode chatMode;
    @Getter @Setter
    private Set<SkinPart> displayedSkinParts;
    @Getter @Setter
    private Hand mainHand;
    @Getter
    private boolean listed = true;
    @Getter @Setter
    private int latency = 0;
    @Getter
    private Component displayName;
    @Getter
    private Component playerListName;

    private int teleportID = 0;
    private boolean teleporting = false;
    private Location teleportLocation;

    private ServerPlayer(final Server server, final PlayerProfile profile, final ClientConnection connection) {
        super(server, EntityType.PLAYER, profile.getUUID());
        this.profile = Objects.requireNonNull(profile, "Player profile can not be null");
        Objects.requireNonNull(connection, "Player's connection can not be null");
        if (connection.getOwner().isPresent())
            throw new IllegalStateException("There can't be multiple players with the same ClientConnection");
        if (connection.getState().orElse(null) != PlayerConnection.ClientState.PLAY)
            throw new IllegalStateException("Player's connection has to be in play state");
        connection.setOwner(this);
        connection.startKeepingAlive();
        this.connection = connection;
        this.displayName = TextComponent.of(getName());
        playerListName = displayName;
    }

    /**
     * Creates new player instance from uninitialized connection,
     * loads information from server's player data container and
     * adds player to the server's manager.
     * @param server server
     * @param profile profile of the player
     * @param connection connection of the player
     * @return created player instance
     */
    public static ServerPlayer spawn(final Server server,
                                     final PlayerProfile profile,
                                     final ClientConnection connection) {
        Objects.requireNonNull(server, "Server can not be null");
        Objects.requireNonNull(profile, "Player profile can not be null");
        Objects.requireNonNull(connection, "Player's connection can not be null");

        final PlayerManager manager = server.getPlayerManager();

        if (connection.getState().orElse(null) != PlayerConnection.ClientState.PLAY) {
            throw new IllegalStateException("Player can't be initialized if their connection isn't in play state");
        }

        if (manager.getPlayer(profile.getUsername()).isPresent() || manager.getPlayer(profile.getUUID()).isPresent()) {
            connection.disconnect(TranslationComponent.of("disconnect.loginFailed"));
            throw new IllegalStateException("Session is already active");
        }

        // Loading NBT Data
        final ServerPlayer player = new ServerPlayer(server, profile, connection);
        if (server.getPlayerDataContainer().exist(player.getUUID())) {
            try {
                final NBTCompound nbtCompound = server.getPlayerDataContainer().getPlayerData(player.getUUID());
                if (nbtCompound != null)
                    player.load(nbtCompound);
            } catch (Exception exception) {
                server.getConsole().warning("Failed to load player data for " + player.getName()
                        + " (" + player.getUUID() + ")");
                server.getExceptionHandler().handle(exception);
            }
        }

        try {
            manager.addPlayer(player);
            final TranslationComponent joinMessage = TranslationComponent.of(
                    "multiplayer.player.joined", TextComponent.of(player.getName())
            ).modify().color(ChatColor.YELLOW).finish();
            manager.getPlayers().forEach(serverPlayer -> serverPlayer.sendMessage(joinMessage));
            server.getConsole().info(ChatColor.YELLOW + player.getDisplayName().toLegacyString() + " joined the game");
            player.init();
            return player;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public void init() {

        final NBTCompound codec = new Codec(
                getServer().getDimensionTypeManager(),
                getServer().getDamageTypeManager(),
                getServer().getBiomeManager(),
                getServer().getMessenger()
        ).toNBT();
        // TODO placeholders for now
        codec.put("minecraft:trim_pattern", Map.of(
                "type", "minecraft:trim_pattern",
                "value", new NBTList()));
        codec.put("minecraft:trim_material", Map.of(
                "type", "minecraft:trim_material",
                "value", new NBTList()));

        final List<String> worlds = new ArrayList<>();
        for (final World world : getServer().getWorldManager().getWorlds())
            worlds.add(world.getName().toString());

        sendPacket(new PacketPlayOutLogin(
                getEntityID(),
                false,
                gamemode,
                previousGamemode,
                worlds,
                codec,
                getWorld().getDimensionType().getName(),
                getWorld().getName(),
                Hashing.sha256().hashLong(getWorld().getSeed()).asLong(),
                getServer().getProperties().getMaxPlayers(),
                getServer().getProperties().getViewDistance(),
                getServer().getProperties().getSimulationDistance(),
                getServer().getProperties().isReducedDebugScreen(),
                true,
                false,
                getWorld().getWorldType() == WorldType.FLAT,
                false,
                null,
                null,
                getPortalCooldown()
        ));

        super.init();

        // Other players
        final Set<PlayerConnection> others = getServer().getConnection().getClients().stream()
                .filter(connection -> connection.getState().orElse(null) == PlayerConnection.ClientState.PLAY)
                .filter(connection -> connection.getOwner().isPresent())
                .filter(connection -> connection != getConnection())
                .collect(Collectors.toSet());

        // Spawn Sequence: https://wiki.vg/Protocol_FAQ#What.27s_the_normal_login_sequence_for_a_client.3F

        // Brand
        sendPacket(PacketPlayOutPluginMessage.getBrandPacket(getServer().getProperties().getServerBrand()));

        // Difficulty
        sendDifficultyChange(getWorld().getDifficulty());

        // Player Abilities (Optional)
        // Set Carried Item
        // Update Recipes
        // Update Tags
        // Entity Event (for the OP permission level)
        // Commands
        // Recipe
        // Player Position

        // Player info
        final EnumSet<PacketPlayOutPlayerInfo.Action> actions = EnumSet.of(
                PacketPlayOutPlayerInfo.Action.ADD_PLAYER,
                PacketPlayOutPlayerInfo.Action.UPDATE_LATENCY
        );
        sendPacket(new PacketPlayOutPlayerInfo(actions, this));
        for (final Player player : getServer().getEntityManager().getEntitiesOfClass(Player.class)) {
            if (player == this)
                continue;
            sendPacket(new PacketPlayOutPlayerInfo(actions, player));
            player.sendPacket(new PacketPlayOutPlayerInfo(actions, this));
        }

        // Set Chunk Cache Center
        // Light Update (One sent for each chunk in a square centered on the player's position)
        // Level Chunk With Light (One sent for each chunk in a square centered on the player's position)
        // World Border (Once the world is finished loading)

        // Set Default Spawn Position
        sendWorldSpawnChange(getWorld().getWorldSpawn());

        // Synchronize Player Position
        synchronizePosition(new EntityPosition(0, 0, 0), EnumSet.allOf(TeleportFlags.class));
        for (final Player player : getServer().getEntityManager().getEntitiesOfClass(Player.class)) {
            if (player == this)
                continue;
            sendPacket(new PacketPlayOutSpawnPlayer(player.getEntityID(), player.getUUID(), player.getLocation()));
            sendPacket(new PacketPlayOutHeadRotation(player.getEntityID(), player.getLocation().getYaw()));
        }
        for (final PlayerConnection other : others) {
            other.send(new PacketPlayOutSpawnPlayer(getEntityID(), getUUID(), getLocation()));
            other.send(new PacketPlayOutHeadRotation(getEntityID(), getLocation().getYaw()));
        }

        // Inventory, entities, etc

        // Gamemode
        sendGamemodeChange(gamemode);
    }

    @Override
    public void remove() {
        if (connection.getState().orElse(null) != PlayerConnection.ClientState.DISCONNECTED)
            throw new IllegalStateException("You can't remove player from server until the connection is closed");
        super.remove();
        getWorld().remove(this);
        getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfoRemove(getUUID()));
        getServer().getPlayerManager().removePlayer(this);
        final TranslationComponent leaveMessage = TranslationComponent.of(
                "multiplayer.player.left", TranslationComponent.of(getName())
        ).modify().color(ChatColor.YELLOW).finish();
        getServer().getPlayerManager().getPlayers().forEach(serverPlayer -> serverPlayer.sendMessage(leaveMessage));
        getServer().getConsole().info(ChatColor.YELLOW + getDisplayName().toLegacyString() + " left the game");
        save();
    }

    @Override
    public String getName() {
        return profile.getUsername();
    }

    @Override
    public void setDisplayName(final @Nullable Component displayName) {
        this.displayName = displayName == null ? TextComponent.of(getName()) : displayName;
    }

    @Override
    public void setPlayerListName(final @Nullable Component playerListName) {
        this.playerListName = playerListName != null ? playerListName : TextComponent.of(getName());
        getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(
                EnumSet.of(PacketPlayOutPlayerInfo.Action.UPDATE_DISPLAY_NAME),
                this
        ));
    }

    @Override
    public Optional<Gamemode> getPreviousGamemode() {
        return Optional.ofNullable(previousGamemode);
    }

    @Override
    public void setGamemode(final Gamemode gamemode) {
        Objects.requireNonNull(gamemode, "Gamemode can not be null");
        previousGamemode = this.gamemode;
        this.gamemode = gamemode;
        sendGamemodeChange(gamemode);
        getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(
                EnumSet.of(PacketPlayOutPlayerInfo.Action.UPDATE_GAMEMODE),
                this
        ));
    }

    @Override
    public void setListed(final boolean listed) {
        this.listed = listed;
        getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(
                EnumSet.of(PacketPlayOutPlayerInfo.Action.UPDATE_LISTED),
                this
        ));
    }

    @Override
    public int execute(final String input) {
        return 0; // TODO
    }

    @Override
    public void sendMessage(final @Nullable UUID source, final Component message, final MessageType type) {
        getServer().getMessenger().sendMessage(this, message, type);
    }

    /**
     * Sends packet to change difficulty.
     * @param difficulty new difficulty
     */
    private void sendDifficultyChange(final Difficulty difficulty) {
        Objects.requireNonNull(difficulty, "Difficulty can not be null");
        sendPacket(new PacketPlayOutChangeDifficulty(difficulty));
    }

    /**
     * Sends packet to change world spawn.
     * @param location new world spawn
     */
    private void sendWorldSpawnChange(final Location location) {
        Objects.requireNonNull(location, "Location can not be null");
        sendPacket(new PacketPlayOutWorldSpawnPosition(location));
    }

    /**
     * Sends packet to change gamemode.
     * @param gamemode new gamemode
     */
    private void sendGamemodeChange(final Gamemode gamemode) {
        Objects.requireNonNull(gamemode, "Gamemode can not be null");
        sendPacket(new PacketPlayOutGameEvent(PacketPlayOutGameEvent.Event.CHANGE_GAMEMODE, gamemode.getID()));
    }

    /**
     * Synchronizes player's position.
     * @param position new position
     * @param flags teleport flags
     */
    public void synchronizePosition(final EntityPosition position, final Set<TeleportFlags> flags) {
        teleporting = true;

        final double x = position.getX() + (flags.contains(TeleportFlags.X) ? getLocation().getX() : 0d);
        final double y = position.getY() + (flags.contains(TeleportFlags.Y) ? getLocation().getY() : 0d);
        final double z = position.getZ() + (flags.contains(TeleportFlags.Z) ? getLocation().getZ() : 0d);
        final float yaw = position.getYaw() + (flags.contains(TeleportFlags.YAW) ? getLocation().getYaw() : 0f);
        final float pitch = position.getPitch() + (flags.contains(TeleportFlags.PITCH) ? getLocation().getPitch() : 0f);

        teleportLocation = new Location(x, y, z, yaw, pitch, getWorld());
        if (++teleportID == Integer.MAX_VALUE)
            teleportID = 0;

        sendPacket(new PacketPlayOutSynchronizePlayerPosition(position, flags, teleportID));
    }

    /**
     * Handles the teleport confirmation of the player.
     * @param teleportID id of teleport
     * @return whether the teleport was successful
     */
    public boolean handleTeleportConfirm(final int teleportID) {
        if (!teleporting || this.teleportID != teleportID) {
            teleporting = false;
            connection.disconnect(TranslationComponent.of("multiplayer.disconnect.invalid_player_movement"));
            return false;
        }
        teleporting = false;
        setLocation(teleportLocation, true);
        teleportLocation = null;
        return true;
    }

    @Override
    public void handleMovement(final EntityPosition position, final boolean onGround) {
        if (teleporting)
            return;

        super.handleMovement(position, onGround);
    }

    @Override
    public NBTCompound toNBT() {
        final NBTCompound nbtCompound = super.toNBT();
        nbtCompound.set("playerGameType", gamemode.getID());
        if (previousGamemode != null)
            nbtCompound.set("previousPlayerGameType", previousGamemode.getID());
        return nbtCompound;
    }

    @Override
    public void load(final NBTCompound nbtCompound) {
        super.load(nbtCompound);
        // TODO replace with default gamemode from server.properties
        gamemode = Gamemode.fromID(nbtCompound.getValue("playerGameType", Gamemode.SURVIVAL.getID()));
        previousGamemode = nbtCompound.containsKey("previousPlayerGameType")
                ? Gamemode.fromID(nbtCompound.getValue("previousPlayerGameType"))
                : null;
    }

    @Override
    public void sendPacket(final Packet packet) {
        getConnection().send(packet);
    }

    @Override
    public void save() {
        getServer().getPlayerDataContainer().savePlayerData(this);
    }

    @Override
    public String toString() {
        return getName();
    }

}
