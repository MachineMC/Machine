package org.machinemc.server.utils;

import lombok.Cleanup;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Utility class for networking related operations.
 */
public final class NetworkUtils {

    private NetworkUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if provided port is available for use or not.
     * @param port port to check for
     * @return true if available
     */
    public static boolean available(final @Range(from = 0, to = 65536) int port) {
        try {
            final @Cleanup ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            final @Cleanup DatagramSocket datagramSocket = new DatagramSocket(port);
            datagramSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
