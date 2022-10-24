package me.pesekjak.machine.file;

import lombok.Getter;
import me.pesekjak.machine.Machine;
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
    private final File playerDataFolder;

    public PlayerDataContainer(Machine server) {
        playerDataFolder = new File(DEFAULT_PLAYER_DATA_FOLDER);
        createFolderIfAbsent();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File createFolderIfAbsent() {
        if (!playerDataFolder.exists())
            playerDataFolder.mkdirs();
        return playerDataFolder;
    }

    private NBTCompound getPlayerData(UUID uuid) {
        File playerDataFile = getPlayerDataFile(uuid);
        return NBTUtils.deserializeNBTFile(playerDataFile) instanceof NBTCompound nbtCompound ? nbtCompound : null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getPlayerDataFile(UUID uuid) {
        File playerDataFile = new File(createFolderIfAbsent(), uuid + ".dat");
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
