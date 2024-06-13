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
package org.machinemc.network.protocol.handshake.serverbound;

import org.jetbrains.annotations.ApiStatus;

/**
 * Next state of the client.
 */
public enum ClientIntent {

    /**
     * Unused client intent with id {@code 0}.
     */
    @ApiStatus.Obsolete UNUSED,

    /**
     * Used for pinging the server in the multiplayer menu.
     */
    STATUS,

    /**
     * Used when the client tries to join.
     */
    LOGIN,

    /**
     * Used when the client tries to transfer from another server.
     */
    TRANSFER

}
