package me.pesekjak.machine.entities;

import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.Setter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.chat.ChatUtils;
import me.pesekjak.machine.network.ClientConnection;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.network.packets.out.PacketPlayLogin;
import me.pesekjak.machine.network.packets.out.PacketPlayOutSystemChatMessage;
import me.pesekjak.machine.network.packets.out.PacketPlayPluginMessage;
import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.world.World;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Player extends LivingEntity {

    @Getter
    private final String username;
    @Getter
    private final ClientConnection connection;

    @Getter @Setter
    private Gamemode gamemode = Gamemode.SURVIVAL; // for now

    public Player(Machine server, @NotNull UUID uuid, @NotNull String username, @NotNull ClientConnection connection) {
        super(server, EntityType.PLAYER, uuid);
        this.username = username;
        setDisplayName(username);
        if(connection.getOwner() != null)
            throw new UnsupportedOperationException("There can't be multiple players with the same ClientConnection");
        this.connection = connection;
        try {
            init();
        } catch (Exception e) {
            connection.disconnect(Component.text("Failed initialization."));
        }
    }

    private void init() throws IOException {
        NBTCompound nbt = NBT.Compound(Map.of(
                "minecraft:dimension_type", getServer().getDimensionTypeManager().toNBT(),
                "minecraft:worldgen/biome", getServer().getBiomeManager().toNBT()));
        List<String> worlds = new ArrayList<>();
        for(World world : getServer().getWorldManager().getWorlds())
            worlds.add(world.getName().toString());
        FriendlyByteBuf playLoginBuf = new FriendlyByteBuf()
                .writeInt(1)
                .writeBoolean(false)
                .writeByte((byte) gamemode.getID())
                .writeByte((byte) -1)
                .writeStringList(worlds, StandardCharsets.UTF_8)
                .writeNBT("", nbt)
                .writeString(getServer().getDefaultWorld().getDimensionType().getName().toString(), StandardCharsets.UTF_8)
                .writeString(getServer().getDefaultWorld().getName().toString(), StandardCharsets.UTF_8)
                .writeLong(Hashing.sha256().hashLong(getServer().getDefaultWorld().getSeed()).asLong())
                .writeVarInt(getServer().getProperties().getMaxPlayers())
                .writeVarInt(8) // TODO Server Properties - View Distance
                .writeVarInt(8) // TODO Server Properties - Simulation Distance
                .writeBoolean(false) // TODO Server Properties - Reduced Debug Screen
                .writeBoolean(true)
                .writeBoolean(false)
                .writeBoolean(false) // TODO World - Is Spawn World Flat
                .writeBoolean(false);
        connection.sendPacket(new PacketPlayLogin(playLoginBuf));

        // TODO Add this as option in server properties
        connection.sendPacket(PacketPlayPluginMessage.getBrandPacket("Machine server"));
    }

    public void sendMessage(String string) {
        sendMessage(ChatUtils.parse(string));
    }

    public void sendMessage(Component component) {
        sendJsonMessage(GsonComponentSerializer.gson().serialize(component));
    }

    private void sendJsonMessage(String json) {
        try {
            json = json.replace(String.valueOf(ChatUtils.COLOR_CHAR), "");
            PacketOut packet = new PacketPlayOutSystemChatMessage(new FriendlyByteBuf()
                    .writeString(json, StandardCharsets.UTF_8)
                    .writeBoolean(false));
            connection.sendPacket(packet);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
