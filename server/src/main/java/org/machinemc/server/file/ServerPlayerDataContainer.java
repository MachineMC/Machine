/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.server.file;

import lombok.AccessLevel;
import lombok.Getter;
import org.machinemc.api.Server;
import org.machinemc.api.entities.Player;
import org.machinemc.api.file.PlayerDataContainer;
import org.machinemc.api.utils.NBTUtils;
import org.machinemc.nbt.NBTCompound;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation of player data container.
 */
public class ServerPlayerDataContainer implements PlayerDataContainer {

    public static final String DEFAULT_PLAYER_DATA_FOLDER = "playerdata";

    @Getter
    private final Server server;
    @Getter(AccessLevel.PRIVATE)
    private final File playerDataFolder;

    public ServerPlayerDataContainer(final Server server, final File folder) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
        playerDataFolder = Objects.requireNonNull(folder, "Player data folder can not be null");
        if (!playerDataFolder.exists() && !playerDataFolder.mkdirs())
            throw new RuntimeException("Can't create the player data folder");
    }

    @Override
    public boolean exist(final UUID uuid) {
        return getPlayerDataFile(uuid, false).exists();
    }

    @Override
    public NBTCompound getPlayerData(final UUID uuid) {
        Objects.requireNonNull(uuid, "UUID can not be null");
        final File playerDataFile = getPlayerDataFile(uuid, true);
        return NBTUtils.deserializeNBTFile(playerDataFile);
    }

    /**
     * Returns player data file for player with given uuid.
     * @param uuid uuid of the player
     * @param create if the file should be created in case it doesn't exist
     * @return player's data file
     */
    private File getPlayerDataFile(final UUID uuid, final boolean create) {
        Objects.requireNonNull(uuid, "UUID can not be null");
        final File playerDataFile = new File(playerDataFolder, uuid + ".dat");
        if (!create) return playerDataFile;
        try {
            if (!playerDataFolder.exists() && !playerDataFolder.mkdirs())
                throw new RuntimeException("Can't create the player data folder");
            if (!playerDataFile.exists() && !playerDataFile.createNewFile())
                throw new RuntimeException("Can't create the player data file for " + uuid);
            return playerDataFile;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Saves players data file with the newest information.
     * @param player player to save data for
     */
    @Override
    public void savePlayerData(final Player player) {
        Objects.requireNonNull(player, "Player can not be null");
        player.serializeNBT(getPlayerDataFile(player.getUUID(), true));
    }

    @Override
    public String toString() {
        return "PlayerDataContainer("
                + "server=" + server
                + ')';
    }

}
