package org.machinemc.server.entities;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
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
import org.machinemc.api.world.Difficulty;
import org.machinemc.api.world.Location;
import org.machinemc.api.world.World;
import org.machinemc.api.world.WorldType;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.server.Machine;
import org.machinemc.server.network.ClientConnection;
import org.machinemc.server.network.packets.out.play.*;
import org.machinemc.server.server.codec.Codec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    @Getter
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
    @Getter @Setter
    private int latency = 0;
    @Getter
    private Component displayName;
    @Getter
    private Component playerListName;

    private ServerPlayer(final Machine server, final PlayerProfile profile, final ClientConnection connection) {
        super(server, EntityType.PLAYER, profile.getUuid());
        this.profile = profile;
        if (connection.getOwner() != null)
            throw new IllegalStateException("There can't be multiple players with the same ClientConnection");
        if (connection.getState() != PlayerConnection.ClientState.PLAY)
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
    public static ServerPlayer spawn(final Machine server,
                                     final PlayerProfile profile,
                                     final ClientConnection connection) {
        final PlayerManager manager = server.getPlayerManager();
        if (connection.getState() != PlayerConnection.ClientState.PLAY) {
            throw new IllegalStateException("Player can't be initialized if their connection isn't in play state");
        }
        if (manager.getPlayer(profile.getUsername()) != null || manager.getPlayer(profile.getUuid()) != null) {
            connection.disconnect(TranslationComponent.of("disconnect.loginFailed"));
            throw new IllegalStateException("Session is already active");
        }
        ServerPlayer player = new ServerPlayer(server, profile, connection);
        if (server.getPlayerDataContainer().exist(player.getUuid())) {
            try {
                final NBTCompound nbtCompound = server.getPlayerDataContainer().getPlayerData(player.getUuid());
                if (nbtCompound != null)
                    player.load(nbtCompound);
            } catch (Exception exception) {
                server.getConsole().warning("Failed to load player data for " + player.getName()
                        + " (" + player.getUuid() + ")");
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
        super.init();

        NBTCompound codec = new Codec(
                getServer().getDimensionTypeManager(),
                getServer().getBiomeManager(),
                getServer().getMessenger()
        ).toNBT();

        List<String> worlds = new ArrayList<>();
        for (World world : getServer().getWorldManager().getWorlds())
            worlds.add(world.getName().toString());

        //noinspection UnstableApiUsage
        sendPacket(new PacketPlayOutLogin(
                getEntityId(),
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
                null
        ));

        sendPacket(PacketPlayOutPluginMessage.getBrandPacket(getServer().getProperties().getServerBrand()));

        // Spawn Sequence: https://wiki.vg/Protocol_FAQ#What.27s_the_normal_login_sequence_for_a_client.3F
        sendDifficultyChange(getWorld().getDifficulty());
        // Player Abilities (Optional)
        // Set Carried Item
        // Update Recipes
        // Update Tags
        // Entity Event (for the OP permission level)
        // Commands
        // Recipe
        // Player Position
        sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.ADD_PLAYER, this));
        for (Player player : getServer().getEntityManager().getEntitiesOfClass(Player.class)) {
            if (player == this)
                continue;
            sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.ADD_PLAYER, player));
            player.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.ADD_PLAYER, this));
        }
        // Set Chunk Cache Center
        // Light Update (One sent for each chunk in a square centered on the player's position)
        // Level Chunk With Light (One sent for each chunk in a square centered on the player's position)
        // World Border (Once the world is finished loading)
        sendWorldSpawnChange(getWorld().getWorldSpawn());
        // Player Position (Required, tells the client they're ready to spawn)
        // Inventory, entities, etc
        sendGamemodeChange(gamemode);
        getWorld().loadPlayer(this);
    }

    @Override
    public void remove() {
        if (connection.getState() != PlayerConnection.ClientState.DISCONNECTED)
            throw new IllegalStateException("You can't remove player from server until the connection is closed");
        super.remove();
        getWorld().unloadPlayer(this);
        getServer().getConnection().broadcastPacket(
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.REMOVE_PLAYER, this)
        );
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
        getServer().getConnection().broadcastPacket(
                new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.Action.UPDATE_DISPLAY_NAME,
                        this)
        );
    }

    @Override
    public void setGamemode(final Gamemode gamemode) {
        previousGamemode = this.gamemode;
        this.gamemode = gamemode;
        sendGamemodeChange(gamemode);
    }

    @Override
    public int execute(final String input) {
        return 0;
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
        sendPacket(new PacketPlayOutChangeDifficulty(difficulty));
    }

    /**
     * Sends packet to change world spawn.
     * @param location new world spawn
     */
    private void sendWorldSpawnChange(final Location location) {
        sendPacket(new PacketPlayOutWorldSpawnPosition(location));
    }

    /**
     * Sends packet to change gamemode.
     * @param gamemode new gamemode
     */
    private void sendGamemodeChange(final Gamemode gamemode) {
        sendPacket(new PacketPlayOutGameEvent(PacketPlayOutGameEvent.Event.CHANGE_GAMEMODE, gamemode.getId()));
    }

    @Override
    public NBTCompound toNBT() {
        NBTCompound nbtCompound = super.toNBT();
        nbtCompound.set("playerGameType", gamemode.getId());
        if (previousGamemode != null)
            nbtCompound.set("previousPlayerGameType", previousGamemode.getId());
        return nbtCompound;
    }

    @Override
    public void load(final NBTCompound nbtCompound) {
        super.load(nbtCompound);
        // TODO replace with default gamemode from server.properties
        gamemode = Gamemode.fromID(nbtCompound.getValue("playerGameType", Gamemode.SURVIVAL.getId()));
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

}
