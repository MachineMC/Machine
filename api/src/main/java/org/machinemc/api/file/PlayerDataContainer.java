package org.machinemc.api.file;

import org.machinemc.api.entities.Player;
import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.Nullable;
import org.machinemc.nbt.NBTCompound;

import java.util.UUID;

/**
 * Represents player data file container.
 */
public interface PlayerDataContainer extends ServerProperty {

    /**
     * Checks if data file exists for player with given uuid.
     * @param uuid uuid of the player
     * @return if data file for the player exists
     */
    boolean exist(UUID uuid);

    /**
     * Returns data of a player as nbt compound.
     * @param uuid uuid of the player
     * @return player's data
     */
    @Nullable NBTCompound getPlayerData(UUID uuid);

    /**
     * Saves data of a player.
     * @param player player to save data for
     */
    void savePlayerData(Player player);

}
