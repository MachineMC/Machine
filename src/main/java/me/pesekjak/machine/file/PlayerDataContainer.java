package me.pesekjak.machine.file;

import lombok.AccessLevel;
import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.server.ServerProperty;
import me.pesekjak.machine.utils.NBTUtils;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataContainer implements ServerProperty {

    public final static String DEFAULT_PLAYER_DATA_FOLDER = "playerdata";

    @Getter(AccessLevel.PRIVATE)
    private final File playerDataFolder;
    @Getter
    private final Machine server;

    public PlayerDataContainer(Machine server) {
        this.server = server;
        playerDataFolder = new File(DEFAULT_PLAYER_DATA_FOLDER);
        if(!playerDataFolder.exists() && !playerDataFolder.mkdirs())
            throw new RuntimeException("Can't create the player data folder");
    }

    public NBTCompound getPlayerData(UUID uuid) {
        File playerDataFile = getPlayerDataFile(uuid);
        return NBTUtils.deserializeNBTFile(playerDataFile) instanceof NBTCompound nbtCompound ? nbtCompound : null;
    }

    private File getPlayerDataFile(UUID uuid) {
        File playerDataFile = new File(new File(DEFAULT_PLAYER_DATA_FOLDER), uuid + ".dat");
        try {
            if(!playerDataFile.exists() && !playerDataFile.createNewFile()) {
                throw new RuntimeException("Can't create the player data file for " + uuid);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return playerDataFile;
    }

    public void savePlayerData(Player player) {
        player.serializeNBT(getPlayerDataFile(player.getUuid()));
    }

}
