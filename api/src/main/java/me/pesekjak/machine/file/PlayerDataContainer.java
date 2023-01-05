package me.pesekjak.machine.file;

import me.pesekjak.machine.entities.Player;
import me.pesekjak.machine.server.ServerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.UUID;

/**
 * Represents player data file container.
 */
public interface PlayerDataContainer extends ServerProperty {

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
