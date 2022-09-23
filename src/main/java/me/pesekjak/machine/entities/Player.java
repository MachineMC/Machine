package me.pesekjak.machine.entities;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chat.ChatMode;
import me.pesekjak.machine.chat.Messenger;
import me.pesekjak.machine.entities.player.Gamemode;
import me.pesekjak.machine.entities.player.Hand;
import me.pesekjak.machine.entities.player.PlayerProfile;
import me.pesekjak.machine.entities.player.SkinPart;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.out.*;
import me.pesekjak.machine.network.packets.out.PacketPlayOutGameEvent.Event;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.Difficulty;
import me.pesekjak.machine.world.Location;
import me.pesekjak.machine.world.World;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    // TODO implement latency when keep alive packet is done
    @Getter
    private int latency = 0;

    public Player(Machine server, @NotNull PlayerProfile profile, @NotNull ClientConnection connection) {
        super(server, EntityType.PLAYER, profile.getUuid());
        this.profile = profile;
        if(connection.getOwner() != null)
            throw new UnsupportedOperationException("There can't be multiple players with the same ClientConnection");
        connection.setOwner(this);
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
        NBTCompound nbt = NBT.Compound(Map.of(
                "minecraft:chat_type", Messenger.CHAT_REGISTRY,
                "minecraft:dimension_type", getServer().getDimensionTypeManager().toNBT(),
                "minecraft:worldgen/biome", getServer().getBiomeManager().toNBT()));
        List<String> worlds = new ArrayList<>();
        for(World world : getServer().getWorldManager().getWorlds())
            worlds.add(world.getName().toString());
        FriendlyByteBuf playLoginBuf = new FriendlyByteBuf()
                .writeInt(getEntityId())
                .writeBoolean(false)
                .writeByte((byte) gamemode.getId())
                .writeByte((byte) -1)
                .writeStringList(worlds, StandardCharsets.UTF_8)
                .writeNBT("", nbt)
                .writeString(getWorld().getDimensionType().getName().toString(), StandardCharsets.UTF_8)
                .writeString(getWorld().getName().toString(), StandardCharsets.UTF_8)
                .writeLong(Hashing.sha256().hashLong(getWorld().getSeed()).asLong())
                .writeVarInt(getServer().getProperties().getMaxPlayers())
                .writeVarInt(8) // TODO Server Properties - View Distance
                .writeVarInt(8) // TODO Server Properties - Simulation Distance
                .writeBoolean(false) // TODO Server Properties - Reduced Debug Screen
                .writeBoolean(true)
                .writeBoolean(false)
                .writeBoolean(false) // TODO World - Is Spawn World Flat
                .writeBoolean(false);
        connection.sendPacket(new PacketPlayOutLogin(playLoginBuf)); // TODO Rework to the second constructor

        // TODO Add this as option in server properties
        connection.sendPacket(PacketPlayOutPluginMessage.getBrandPacket("Machine server"));

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
            getConnection().sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.Action.ADD_PLAYER, player));
        }
        // Set Chunk Cache Center
        // Light Update (One sent for each chunk in a square centered on the player's position)
        // Level Chunk With Light (One sent for each chunk in a square centered on the player's position)
        // World Border (Once the world is finished loading)
        sendWorldSpawnChange(getWorld().getWorldSpawn());
        // Player Position (Required, tells the client they're ready to spawn)
        // Inventory, entities, etc
        sendGamemodeChange(gamemode);
    }

    public String getName() {
        return profile.getUsername();
    }

    public String getUsername() {
        return profile.getUsername();
    }

    public void setGamemode(Gamemode gamemode) {
        try {
            previousGamemode = this.gamemode;
            this.gamemode = gamemode;
            sendGamemodeChange(gamemode);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private void sendDifficultyChange(Difficulty difficulty) throws IOException {
        connection.sendPacket(new PacketPlayOutChangeDifficulty(difficulty));
    }

    @Override
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        sendSystem(message);
    }

    // TODO Move this to Messenger class + handle if player can display the message (chat settings),
    //  that means implementing Client Information packet
    private void sendSystem(Component message) {
        try {
            connection.sendPacket(new PacketPlayOutSystemChatMessage(message, false));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void sendWorldSpawnChange(Location location) throws IOException {
        connection.sendPacket(new PacketPlayOutWorldSpawnPosition(location));
    }

    private void sendGamemodeChange(Gamemode gamemode) throws IOException {
        connection.sendPacket(new PacketPlayOutGameEvent(Event.CHANGE_GAMEMODE, gamemode.getId()));
    }

}
