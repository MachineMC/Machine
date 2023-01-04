package me.pesekjak.machine.file;

import lombok.AccessLevel;
import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.utils.NBTUtils;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Default implementation of player data container
 */
public class PlayerDataContainerImpl implements PlayerDataContainer {

    public final static String DEFAULT_PLAYER_DATA_FOLDER = "playerdata";

    @Getter(AccessLevel.PRIVATE)
    private final @NotNull File playerDataFolder;
    @Getter
    private final @NotNull Machine server;

    public PlayerDataContainerImpl(@NotNull Machine server) {
        this.server = server;
        playerDataFolder = new File(DEFAULT_PLAYER_DATA_FOLDER);
        if(!playerDataFolder.exists() && !playerDataFolder.mkdirs())
            throw new RuntimeException("Can't create the player data folder");
    }

    @Override
    public boolean exist(@NotNull UUID uuid) {
        return getPlayerDataFile(uuid, false).exists();
    }

    @Override
    public NBTCompound getPlayerData(@NotNull UUID uuid) {
        File playerDataFile = getPlayerDataFile(uuid, true);
        return NBTUtils.deserializeNBTFile(playerDataFile);
    }

    /**
     * Returns player data file for player with given uuid.
     * @param uuid uuid of the player
     * @param create if the file should be created in case it doesn't exist
     * @return player's data file
     */
    private @NotNull File getPlayerDataFile(@NotNull UUID uuid, boolean create) {
        File playerDataFile = new File(new File(DEFAULT_PLAYER_DATA_FOLDER), uuid + ".dat");
        if(!create) return playerDataFile;
        try {
            if(!playerDataFile.exists() && !playerDataFile.createNewFile()) {
                throw new RuntimeException("Can't create the player data file for " + uuid);
            }
            return playerDataFile;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves players data file with the newest information
     * @param player player to save data for
     */
    @Override
    public void savePlayerData(@NotNull Player player) {
        player.serializeNBT(getPlayerDataFile(player.getUuid(), true));
    }

}
