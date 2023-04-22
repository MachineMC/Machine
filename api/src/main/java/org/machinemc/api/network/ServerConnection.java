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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.api.network;

import io.netty.channel.ChannelFuture;
import org.machinemc.api.network.packets.Packet;
import org.machinemc.api.server.ServerProperty;
import org.jetbrains.annotations.*;

import java.util.Set;

/**
 * Represents server's connection.
 */
public interface ServerConnection extends ServerProperty {

    /**
     * @return server's ip
     */
    String getIp();

    /**
     * @return server's port
     */
    @Range(from = 0, to = 65536) int getPort();

    /**
     * @return all player connections connected to the server
     */
    @Unmodifiable Set<PlayerConnection> getClients();

    /**
     * Starts accepting client connections.
     * @return open channel future
     */
    ChannelFuture start();

    /**
     * Closes the server connection.
     * @return close channel future
     */
    ChannelFuture close();

    /**
     * Sends a packet to all clients connected to the server.
     * @param packet packet to send
     */
    void broadcastPacket(Packet packet);

    /**
     * Disconnects a player connection from the server.
     * @param connection connection to disconnect
     * @return close channel future
     */
    ChannelFuture disconnect(PlayerConnection connection);

}
