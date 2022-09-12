package me.pesekjak.machine.entities;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chat.Messenger;
import me.pesekjak.machine.entities.player.ChatMode;
import me.pesekjak.machine.entities.player.Hand;
import me.pesekjak.machine.entities.player.SkinPart;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.out.*;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.BlockPosition;
import me.pesekjak.machine.world.Difficulty;
import me.pesekjak.machine.world.World;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Player extends LivingEntity implements Audience {

    @Getter
    private final String name;
    @Getter
    private final ClientConnection connection;

    @Getter
    private Gamemode gamemode = Gamemode.CREATIVE; // for now

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

    public Player(Machine server, @NotNull UUID uuid, @NotNull String name, @NotNull ClientConnection connection) {
        super(server, EntityType.PLAYER, uuid);
        if(connection.getOwner() != null)
            throw new UnsupportedOperationException("There can't be multiple players with the same ClientConnection");
        this.name = name;
        setDisplayName(Component.text(name));
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
                .writeByte((byte) gamemode.getID())
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
        connection.sendPacket(new PacketPlayOutLogin(playLoginBuf));

        // TODO Add this as option in server properties
        connection.sendPacket(PacketPlayOutPluginMessage.getBrandPacket("Machine server"));

        sendDifficultyChange(getWorld().getDifficulty());
        sendWorldSpawnChange(new BlockPosition(0, 0, 0), 0.0F);
        sendGamemodeChange(gamemode);
    }

    public void setGamemode(Gamemode gamemode) {
        try {
            this.gamemode = gamemode;
            sendGamemodeChange(gamemode);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDifficultyChange(Difficulty difficulty) throws IOException {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeByte((byte) difficulty.getId())
                .writeBoolean(true);
        connection.sendPacket(new PacketPlayOutChangeDifficulty(buf));
    }

    @Override
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        sendSystem(message);
    }

    // TODO Move this to Messenger class + handle if player can display the message (chat settings),
    //  that means implementing Client Information packet
    private void sendSystem(Component message) {
        try {
            connection.sendPacket(new PacketPlayOutSystemChatMessage(
                    new FriendlyByteBuf()
                            .writeComponent(message)
                            .writeBoolean(false)
            ));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void sendWorldSpawnChange(BlockPosition position, float angle) throws IOException {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeBlockPos(position)
                .writeFloat(angle);
        connection.sendPacket(new PacketPlayOutWorldSpawnPosition(buf));
    }

    private void sendGamemodeChange(Gamemode gamemode) throws IOException {
        FriendlyByteBuf buf = new FriendlyByteBuf()
                .writeByte((byte) 3) // TODO Enum for the event
                .writeFloat(gamemode.getID());
        connection.sendPacket(new PacketPlayOutGameEvent(buf));
    }

}
