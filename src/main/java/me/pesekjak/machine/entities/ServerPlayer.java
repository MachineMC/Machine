package me.pesekjak.machine.entities;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chat.ChatColor;
import me.pesekjak.machine.chat.ChatMode;
import me.pesekjak.machine.chat.ChatUtils;
import me.pesekjak.machine.entities.player.Gamemode;
import me.pesekjak.machine.entities.player.Hand;
import me.pesekjak.machine.entities.player.PlayerProfileImpl;
import me.pesekjak.machine.entities.player.SkinPart;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.out.play.*;
import me.pesekjak.machine.network.packets.out.play.PacketPlayOutGameEvent.Event;
import me.pesekjak.machine.server.PlayerManagerImpl;
import me.pesekjak.machine.server.codec.Codec;
import me.pesekjak.machine.world.*;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServerPlayer extends ServerLivingEntity implements Player {

    @Getter
    private final ClientConnection connection;
    @Getter @Setter
    private PlayerProfileImpl profile;

    @Getter
    private Gamemode gamemode = Gamemode.CREATIVE; // for now
    @Getter @Nullable
    private Gamemode previousGamemode = null;

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

    private ServerPlayer(Machine server, @NotNull PlayerProfileImpl profile, @NotNull ClientConnection connection) {
        super(server, EntityType.PLAYER, profile.getUuid());
        this.profile = profile;
        if(connection.getOwner() != null)
            throw new IllegalStateException("There can't be multiple players with the same ClientConnection");
        if(connection.getClientState() != ClientConnection.ClientState.PLAY)
            throw new IllegalStateException("Player's connection has to be in play state");
        connection.setOwner(this);
        connection.startKeepingAlive();
        this.connection = connection;
        this.displayName = Component.text(getName());
        playerListName = displayName;
    }

    public static ServerPlayer spawn(Machine server, @NotNull PlayerProfileImpl profile, @NotNull ClientConnection connection) {
        final PlayerManagerImpl manager = server.getPlayerManager();
        if(connection.getClientState() != ClientConnection.ClientState.PLAY) {
            throw new IllegalStateException("Player can't be initialized if their connection isn't in play state");
        }
        if(manager.getPlayer(profile.getUsername()) != null || manager.getPlayer(profile.getUuid()) != null) {
            connection.disconnect(Component.translatable("disconnect.loginFailed"));
            throw new IllegalStateException("Session is already active");
        }
        ServerPlayer player = new ServerPlayer(server, profile, connection);
        try {
            final NBTCompound nbtCompound = server.getPlayerDataContainer().getPlayerData(player.getUuid());
            if(nbtCompound != null)
                player.load(nbtCompound);
        } catch (Exception ignored) {
            server.getConsole().warning("Failed to load player data for " + player.getName() + " (" + player.getUuid() + ")");
        }
        try {
            manager.addPlayer(player);
            final Component joinMessage = Component.translatable("multiplayer.player.joined", Component.text(player.getName())).style(ChatColor.YELLOW.asStyle());
            manager.getPlayers().forEach(serverPlayer -> serverPlayer.sendMessage(joinMessage));
            server.getConsole().info(ChatColor.YELLOW + ChatUtils.componentToString(player.getDisplayName()) + " joined the game");
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
        for(World world : getServer().getWorldManager().getWorlds())
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
        try {
            if(connection.getClientState() != ClientConnection.ClientState.DISCONNECTED)
                throw new IllegalStateException("You can't remove player from server until the connection is closed");
            super.remove();
            getWorld().unloadPlayer(this);
            getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.REMOVE_PLAYER, this));
            getServer().getPlayerManager().removePlayer(this);
            final Component leaveMessage = Component.translatable("multiplayer.player.left", Component.text(getName())).style(ChatColor.YELLOW.asStyle());
            getServer().getPlayerManager().getPlayers().forEach(serverPlayer -> serverPlayer.sendMessage(leaveMessage));
            getServer().getConsole().info(ChatColor.YELLOW + ChatUtils.componentToString(getDisplayName()) + " left the game");
            save();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull String getName() {
        return profile.getUsername();
    }

    public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName == null ? Component.text(getName()) : displayName;
    }

    public void setPlayerListName(@Nullable Component playerListName) {
        if (playerListName == null)
            playerListName = Component.text(getName());
        this.playerListName = playerListName;
        try {
            getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.UPDATE_DISPLAY_NAME, this));
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setGamemode(@NotNull Gamemode gamemode) {
        previousGamemode = this.gamemode;
        this.gamemode = gamemode;
        sendGamemodeChange(gamemode);
    }

    @Override
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        getServer().getMessenger().sendMessage(this, message, type);
    }

    private void sendDifficultyChange(Difficulty difficulty) {
        sendPacket(new PacketPlayOutChangeDifficulty(difficulty));
    }

    private void sendWorldSpawnChange(Location location) {
        sendPacket(new PacketPlayOutWorldSpawnPosition(location));
    }

    private void sendGamemodeChange(Gamemode gamemode) {
        sendPacket(new PacketPlayOutGameEvent(Event.CHANGE_GAMEMODE, gamemode.getId()));
    }

    @Override
    public @NotNull NBTCompound toNBT() {
        MutableNBTCompound nbtCompound = super.toNBT().toMutableCompound();
        nbtCompound.setInt("playerGameType", gamemode.getId());
        if (previousGamemode != null)
            nbtCompound.setInt("previousPlayerGameType", previousGamemode.getId());
        return nbtCompound.toCompound();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void load(@NotNull NBTCompound nbtCompound) {
        super.load(nbtCompound);
        gamemode = Gamemode.fromID(nbtCompound.contains("playerGameType") ? nbtCompound.getInt("playerGameType") : Gamemode.SURVIVAL.getId()); // TODO replace with default gamemode from server.properties
        previousGamemode = nbtCompound.contains("previousPlayerGameType") ? Gamemode.fromID(nbtCompound.getInt("previousPlayerGameType")) : null;
    }

    public void save() {
        getServer().getPlayerDataContainer().savePlayerData(this);
    }
}
