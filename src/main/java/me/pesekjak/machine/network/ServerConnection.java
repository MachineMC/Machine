package me.pesekjak.machine.network;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.network.packets.PacketFactory;
import me.pesekjak.machine.network.packets.PacketOut;
import me.pesekjak.machine.server.ServerProperty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServerConnection extends Thread implements ServerProperty, AutoCloseable {

    // TODO move this somewhere else, probably Machine class?
    public final static int TPS = 20;
    public final static int READ_IDLE_TIMEOUT = 30000;
    public final static int KEEP_ALIVE_FREQ = 20000;

    @Getter
    private final Machine server;
    @Getter
    private final List<ClientConnection> clients = new ArrayList<>();
    @Getter
    private final int port;
    @Getter
    private ServerSocket socket;
    protected ScheduledExecutorService executor; // TODO move this somewhere else, preferably our own Scheduler impl
    private boolean running;

    public ServerConnection(Machine server) {
        this.server = server;
        port = server.getProperties().getServerPort();
        executor = Executors.newSingleThreadScheduledExecutor();
        start();
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket(port, 50, InetAddress.getByName("localhost"));
            server.getConsole().info("Server is listening on '" + socket.getInetAddress().getHostName() + ":" + socket.getLocalPort() + "'");
            running = true;
            while(running) {
                Socket connection = socket.accept();
                ClientConnection sc = new ClientConnection(server, connection);
                clients.add(sc);
                sc.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void close() {
        if(!running)
            throw new IllegalStateException("Server connection isn't running");
        running = false;
        executor.shutdown();
        try {
            socket.close();
        } catch (IOException ignored) { }
    }

    public void broadcastPacket(PacketOut packet) throws IOException {
        ClientConnection.ClientState state = ClientConnection.ClientState.fromState(PacketFactory.getStateFromPacket(packet.getClass()));
        for(ClientConnection client : clients) {
            if(client.getClientState() == state) client.sendPacket(packet);
        }
    }

    public void disconnect(ClientConnection connection) {
        if(connection.getClientState() != ClientConnection.ClientState.DISCONNECTED)
            connection.disconnect();
        clients.remove(connection);
    }

}
