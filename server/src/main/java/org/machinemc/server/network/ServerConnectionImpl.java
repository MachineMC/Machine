package org.machinemc.server.network;

import lombok.Getter;
import org.machinemc.server.Machine;
import org.machinemc.api.network.PlayerConnection;
import org.machinemc.api.network.ServerConnection;
import org.machinemc.api.network.packets.Packet;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Default implementation of the server connection.
 */
public class ServerConnectionImpl extends Thread implements ServerConnection {

    public final static int READ_IDLE_TIMEOUT = 30000;
    public final static int KEEP_ALIVE_FREQ = 20000;

    @Getter
    private final @NotNull Machine server;
    private final Set<PlayerConnection> clients = new CopyOnWriteArraySet<>();
    @Getter
    private final @NotNull String ip;
    @Getter
    private final int port;
    @Getter
    private @Nullable ServerSocket socket;
    private boolean running;

    public ServerConnectionImpl(@NotNull Machine server) {
        if(server.isRunning())
            throw new IllegalStateException();
        this.server = server;
        ip = server.getProperties().getServerIp();
        port = server.getProperties().getServerPort();
        start();
    }

    /**
     * Starts listening to the clients.
     */
    @Override
    public void run() {
        try {
            socket = new ServerSocket(port, 50, InetAddress.getByName(ip));
            server.getConsole().info("Server is listening on '" + socket.getInetAddress().getHostName() + ":" + socket.getLocalPort() + "'");
            running = true;
            while(running) {
                try {
                    Socket connection = socket.accept();
                    ClientConnection sc = new ClientConnection(server, connection);
                    clients.add(sc);
                    sc.start();
                } catch (Exception exception) {
                    if(server.isRunning()) // preventing socket exception when closed
                        server.getExceptionHandler().handle(exception);
                }
            }
        } catch (IOException exception) {
            server.getExceptionHandler().handle(exception);
            System.exit(2);
        }
    }

    /**
     * Closes the server connection.
     */
    @Override
    public void close() {
        if(!running)
            throw new IllegalStateException("Server connection isn't running");
        running = false;
        try {
            if(socket != null) socket.close();
        } catch (IOException ignored) { }
    }

    /**
     * @return list of all connected clients
     */
    @Override
    public @NotNull Set<PlayerConnection> getClients() {
        return Collections.unmodifiableSet(clients);
    }

    /**
     * Sends a packet to all clients with state matching the packet's state.
     * @param packet packet that will be sent
     */
    @Override
    public void broadcastPacket(@NotNull Packet packet) throws IOException {
        final Set<ClientConnection.ClientState> states = Arrays.stream(PlayerConnection.ClientState.fromState(packet.getPacketState()))
                .collect(Collectors.toSet());
        for(PlayerConnection client : clients) {
            if(states.contains(client.getClientState())) client.sendPacket(packet);
        }
    }

    /**
     * Disconnects the client connection.
     * @param connection client connection to disconnect
     */
    public void disconnect(@NotNull PlayerConnection connection) {
        if(connection.getClientState() != ClientConnection.ClientState.DISCONNECTED)
            connection.disconnect(Component.translatable("disconnect.disconnected"));
        clients.remove(connection);
    }

}
