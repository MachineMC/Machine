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
