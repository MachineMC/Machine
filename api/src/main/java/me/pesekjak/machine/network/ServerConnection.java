package me.pesekjak.machine.network;

import me.pesekjak.machine.network.packets.Packet;
import me.pesekjak.machine.server.ServerProperty;
import org.jetbrains.annotations.*;

import java.io.IOException;
import java.util.Set;

/**
 * Represents server's connection.
 */
public interface ServerConnection extends ServerProperty, AutoCloseable {

    /**
     * @return server's ip
     */
    @NotNull @NonNls String getIp();

    /**
     * @return server's port
     */
    @Range(from = 0, to = 65536) int getPort();

    /**
     * @return all player connections connected to the server
     */
    @Unmodifiable @NotNull Set<PlayerConnection> getClients();

    /**
     * Starts accepting client connections.
     */
    @Async.Execute
    void start();

    /**
     * Closes the server connection.
     */
    void close();

    /**
     * Sends a packet to all clients connected to the server.
     * @param packet packet to send
     * @throws IOException if an I/O error occurs during writing the bytes
     */
    void broadcastPacket(@NotNull Packet packet) throws IOException;

    /**
     * Disconnects a player connection from the server.
     * @param connection connection to disconnect
     */
    void disconnect(@NotNull PlayerConnection connection);

}
