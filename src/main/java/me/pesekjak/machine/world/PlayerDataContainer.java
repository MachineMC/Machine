package me.pesekjak.machine.world;

import lombok.Getter;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.utils.NBTUtils;
import me.pesekjak.machine.utils.UUIDUtils;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDataContainer {

    public final static String DEFAULT_PLAYER_DATA_FOLDER = "playerdata";

    private final HashMap<UUID, NBTCompound> container = new HashMap<>();
    @Getter
    private final PersistentWorld world;
    @Getter
    private final File playerDataFolder;

    public PlayerDataContainer(PersistentWorld world) {
        this.world = world;
        playerDataFolder = new File(world.getFolderName(), DEFAULT_PLAYER_DATA_FOLDER);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createPlayerDataFolderIfAbsent() {
        if (!playerDataFolder.exists())
            playerDataFolder.mkdirs();
        return playerDataFolder;
    }

    public void updateContainer() {
        container.clear();
        createPlayerDataFolderIfAbsent();
        String[] files = playerDataFolder.list();
        if (files == null)
            return;
        for (String fileName : files) {
            if (!fileName.endsWith(".dat"))
                continue;
            UUID uuid = UUIDUtils.uuidFromString(fileName.replace(".dat", ""));
            container.put(uuid, getPlayerData(uuid));
        }
    }

    private NBTCompound getPlayerData(UUID uuid) {
        File playerDataFile = getPlayerDataFile(uuid);
        return NBTUtils.deserializeNBTFile(playerDataFile) instanceof NBTCompound nbtCompound ? nbtCompound : null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getPlayerDataFile(UUID uuid) {
        File playerDataFile = new File(createPlayerDataFolderIfAbsent(), uuid + ".dat");
        try {
            if (!playerDataFile.exists())
                playerDataFile.createNewFile();
        }
        catch (IOException e) {
            return null;
        }
        return playerDataFile;
    }

    public NBTCompound getPlayerData(Player player) {
        return container.get(player.getUuid());
    }

    public void savePlayerData(Player player) {
        NBTCompound nbtCompound = player.serializeNBT(getPlayerDataFile(player.getUuid()));
        container.put(player.getUuid(), nbtCompound);
    }

}
