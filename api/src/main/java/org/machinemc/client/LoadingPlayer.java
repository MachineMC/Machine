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
package org.machinemc.client;

import org.machinemc.entity.player.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a player in configuration phase.
 * <p>
 * To access the API specific to play state, use {@link #switchToGame()}.
 * <p>
 * There can always exist only one active instance of {@link Player}
 * and {@link LoadingPlayer}, and both can not exist at the same time.
 */
public interface LoadingPlayer extends Client {

    /**
     * Switches the player from configuration to playing state.
     * <p>
     * This action will make this loading player instance inactive.
     *
     * @return player instance
     */
    CompletableFuture<Player> switchToGame();

}
