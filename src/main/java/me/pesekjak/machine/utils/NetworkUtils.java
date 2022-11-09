package me.pesekjak.machine.utils;

import lombok.Cleanup;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public final class NetworkUtils {

    private NetworkUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if provided port is available for use or not.
     * @param port port to check for
     * @return true if available
     */
    public static boolean available(int port) {
        try {
            @Cleanup ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            @Cleanup DatagramSocket datagramSocket = new DatagramSocket(port);
            datagramSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
