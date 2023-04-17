package org.machinemc.server.file;

import lombok.AccessLevel;
import lombok.Getter;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.server.Machine;
import org.machinemc.api.entities.Player;
import org.machinemc.api.file.PlayerDataContainer;
import org.machinemc.api.utils.NBTUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Default implementation of player data container.
 */
public class PlayerDataContainerImpl implements PlayerDataContainer {

    public static final String DEFAULT_PLAYER_DATA_FOLDER = "playerdata";

    @Getter(AccessLevel.PRIVATE)
    private final File playerDataFolder;
    @Getter
    private final Machine server;

    public PlayerDataContainerImpl(final Machine server) {
        this.server = server;
        playerDataFolder = new File(DEFAULT_PLAYER_DATA_FOLDER);
        if (!playerDataFolder.exists() && !playerDataFolder.mkdirs())
            throw new RuntimeException("Can't create the player data folder");
    }

    @Override
    public boolean exist(final UUID uuid) {
        return getPlayerDataFile(uuid, false).exists();
    }

    @Override
    public NBTCompound getPlayerData(final UUID uuid) {
        File playerDataFile = getPlayerDataFile(uuid, true);
        return NBTUtils.deserializeNBTFile(playerDataFile);
    }

    /**
     * Returns player data file for player with given uuid.
     * @param uuid uuid of the player
     * @param create if the file should be created in case it doesn't exist
     * @return player's data file
     */
    private File getPlayerDataFile(final UUID uuid, final boolean create) {
        File playerDataFile = new File(new File(DEFAULT_PLAYER_DATA_FOLDER), uuid + ".dat");
        if (!create) return playerDataFile;
        try {
            if (!playerDataFolder.exists() && !playerDataFolder.mkdirs())
                throw new RuntimeException("Can't create the player data folder");
            if (!playerDataFile.exists() && !playerDataFile.createNewFile())
                throw new RuntimeException("Can't create the player data file for " + uuid);
            return playerDataFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves players data file with the newest information.
     * @param player player to save data for
     */
    @Override
    public void savePlayerData(final Player player) {
        player.serializeNBT(getPlayerDataFile(player.getUuid(), true));
    }

}
