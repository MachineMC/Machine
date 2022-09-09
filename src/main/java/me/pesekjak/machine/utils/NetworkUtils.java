package me.pesekjak.machine.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class NetworkUtils {

    /**
     * Checks if provided port is available for use or not.
     * @param port port to check for
     * @return true if available
     */
    public static boolean available(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (ds != null) ds.close();
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ignored) { }
            }
        }
    }

}
