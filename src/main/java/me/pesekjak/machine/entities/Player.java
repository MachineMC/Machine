package me.pesekjak.machine.entities;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chat.ChatMode;
import me.pesekjak.machine.entities.player.Gamemode;
import me.pesekjak.machine.entities.player.Hand;
import me.pesekjak.machine.entities.player.PlayerProfile;
import me.pesekjak.machine.entities.player.SkinPart;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.*;
import me.pesekjak.machine.network.packets.out.PacketPlayOutGameEvent.Event;
import me.pesekjak.machine.server.NBTSerializable;
import me.pesekjak.machine.server.codec.Codec;
import me.pesekjak.machine.world.Difficulty;
import me.pesekjak.machine.world.Location;
import me.pesekjak.machine.world.World;
import net.kyori.adventure.audience.Audience;
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

public class Player extends LivingEntity implements Audience, NBTSerializable {

    @Getter
    private final ClientConnection connection;
    @Getter @Setter
    private PlayerProfile profile;

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

    private Player(Machine server, @NotNull PlayerProfile profile, @NotNull ClientConnection connection) {
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

    public static Player spawn(Machine server, @NotNull PlayerProfile profile, @NotNull ClientConnection connection) {
        Player player = new Player(server, profile, connection);
        NBTCompound nbtCompound = server.getPlayerDataContainer().getPlayerData(player);
        if (nbtCompound != null)
            player.load(nbtCompound);
        try {
            player.init();
            return player;
        } catch (Exception e) {
            e.printStackTrace();
            connection.disconnect(Component.text("Failed initialization"));
        }
        return null;
    }

    @Override
    protected void init() throws IOException {
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
                8, // TODO Server Properties - View Distance
                8, // TODO Server Properties - Simulation Distance
                false, // TODO Server Properties - Reduced Debug Screen
                true,
                false,
                false // TODO World - Is Spawn World Flat
        ));

        // TODO Add this as option in server properties
        sendPacket(PacketPlayOutPluginMessage.getBrandPacket("Machine server"));

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
        getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.ADD_PLAYER, this));
        for (Player player : getServer().getEntityManager().getEntitiesOfClass(Player.class)) {
            if (player == this)
                continue;
            sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.ADD_PLAYER, player));
        }
        // Set Chunk Cache Center
        // Light Update (One sent for each chunk in a square centered on the player's position)
        // Level Chunk With Light (One sent for each chunk in a square centered on the player's position)
        // World Border (Once the world is finished loading)
//        sendWorldSpawnChange(getWorld().getWorldSpawn());
        // Player Position (Required, tells the client they're ready to spawn)
        // Inventory, entities, etc
        sendGamemodeChange(gamemode);
    }

    @Override
    public void remove() {
        try {
            super.remove();
            getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.REMOVE_PLAYER, this));
            save();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPacket(PacketOut packet) {
        try {
            connection.sendPacket(packet);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String getName() {
        return profile.getUsername();
    }

    public String getUsername() {
        return profile.getUsername();
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName == null ? Component.text(getName()) : displayName;
    }

    public void setPlayerListName(Component playerListName) {
        if (playerListName == null)
            playerListName = Component.text(getName());
        this.playerListName = playerListName;
        try {
            getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.UPDATE_DISPLAY_NAME, this));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGamemode(Gamemode gamemode) {
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
    public NBTCompound toNBT() {
        MutableNBTCompound nbtCompound = super.toNBT().toMutableCompound();
        nbtCompound.setInt("playerGameType", gamemode.getId());
        if (previousGamemode != null)
            nbtCompound.setInt("previousPlayerGameType", previousGamemode.getId());
        return nbtCompound.toCompound();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void load(NBTCompound nbtCompound) {
        super.load(nbtCompound);
        gamemode = Gamemode.fromID(nbtCompound.contains("playerGameType") ? nbtCompound.getInt("playerGameType") : Gamemode.SURVIVAL.getId()); // TODO replace with default gamemode from server.properties
        previousGamemode = nbtCompound.contains("previousPlayerGameType") ? Gamemode.fromID(nbtCompound.getInt("previousPlayerGameType")) : null;
    }

    protected void save() {
        getServer().getPlayerDataContainer().savePlayerData(this);
    }
}
