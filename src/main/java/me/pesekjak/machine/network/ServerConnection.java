package me.pesekjak.machine.network;

import lombok.Getter;
import me.pesekjak.machine.Machine;
import me.pesekjak.machine.server.ServerProperty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerConnection extends Thread implements ServerProperty {

    @Getter
    private final Machine server;
    @Getter
    private final List<ClientConnection> clients = new ArrayList<>();
    @Getter
    private final int port;
    @Getter
    private ServerSocket socket;

    public ServerConnection(Machine server) {
        this.server = server;
        port = server.getProperties().getServerPort();
        start();
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket(port, 50, InetAddress.getByName("localhost"));
            server.getConsole().info("Server is listening on '" + socket.getInetAddress().getHostName() + ":" + socket.getLocalPort() + "'");
            while(true) {
                Socket connection = socket.accept();
                ClientConnection sc = new ClientConnection(server, connection);
                clients.add(sc);
                sc.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
