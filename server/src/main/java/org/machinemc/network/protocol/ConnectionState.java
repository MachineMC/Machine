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
package org.machinemc.network.protocol;

/**
 * Represents a connection state of a client.
 * <p>
 * Packets are split into multiple groups, differentiated by
 * the connection states.
 */
public enum ConnectionState {

    /**
     * Handshaking client state.
     * <p>
     * Causes the client to switch into the target state, takes care of server pings and
     * initial connection (handshake).
     */
    HANDSHAKING,

    /**
     * Status client state.
     * <p>
     * Used for answering the server list pings.
     */
    STATUS,

    /**
     * Login client state.
     * <p>
     * Takes care of the authentication process.
     */
    LOGIN,

    /**
     * Configuration client state.
     * <p>
     * Used for initial client configuration.
     */
    CONFIGURATION,

    /**
     * Play client state.
     * <p>
     * Used by most packets, while the client is actively playing on the server.
     */
    PLAY

}
