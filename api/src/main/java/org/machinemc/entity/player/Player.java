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
package org.machinemc.entity.player;

import org.machinemc.client.Client;
import org.machinemc.client.LoadingPlayer;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a player actively playing on the server.
 * <p>
 * To access the API specific to configuration state, use {@link #switchToConfiguration()}.
 */
public interface Player extends Client {

    /**
     * Switches the player from playing to configuration state.
     * <p>
     * This action will make this player instance inactive.
     *
     * @return loading player instance
     */
    CompletableFuture<LoadingPlayer> switchToConfiguration();

}
