package me.pesekjak.machine.file;

import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.server.ServerProperty;
import mx.kenzie.nbt.NBTCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    boolean exist(@NotNull UUID uuid);

    /**
     * Returns data of a player as nbt compound.
     * @param uuid uuid of the player
     * @return player's data
     */
    @Nullable NBTCompound getPlayerData(@NotNull UUID uuid);

    /**
     * Saves data of a player.
     * @param player player to save data for
     */
    void savePlayerData(@NotNull Player player);

}
