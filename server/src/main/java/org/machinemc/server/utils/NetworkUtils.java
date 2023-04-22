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
