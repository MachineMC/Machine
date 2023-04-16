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
