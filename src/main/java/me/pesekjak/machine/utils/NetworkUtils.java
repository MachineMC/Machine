package me.pesekjak.machine.utils;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Utility class for networking related operations.
 */
@UtilityClass
public class NetworkUtils {

    /**
     * Checks if provided port is available for use or not.
     * @param port port to check for
     * @return true if available
     */
    public static boolean available(@Range(from = 0, to = 65536) int port) {
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
