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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Player extends LivingEntity implements Audience {

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

    public Player(Machine server, @NotNull PlayerProfile profile, @NotNull ClientConnection connection) {
        super(server, EntityType.PLAYER, profile.getUuid());
        this.profile = profile;
        if(connection.getOwner() != null)
            throw new IllegalStateException("There can't be multiple players with the same ClientConnection");
        if(connection.getClientState() != ClientConnection.ClientState.PLAY)
            throw new IllegalStateException("Player's connection has to be in play state");
        connection.setOwner(this);
        connection.startKeepingAlive();
        setDisplayName(Component.text(profile.getUsername()));
        this.connection = connection;
        try {
            init();
        } catch (IOException e) {
            connection.disconnect(Component.text("Failed initialization."));
        }
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

        sendPacket(new PacketPlayOutLogin(
                getEntityId(),
                false,
                gamemode,
                null,
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
//        sendWorldSpawnChange(getWorld().getWorldSpawn()); // TODO fix world spawn change packet
        // Player Position (Required, tells the client they're ready to spawn)
        // Inventory, entities, etc
        sendGamemodeChange(gamemode);
    }

    @Override
    public void remove() {
        try {
            super.remove();
            getServer().getConnection().broadcastPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.REMOVE_PLAYER, this));
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

    public String getName() {
        return profile.getUsername();
    }

    public String getUsername() {
        return profile.getUsername();
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

}
