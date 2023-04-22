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
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.api.auth;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;

import java.time.Instant;

/**
 * Represents a signature of a message for new 1.19 chat system.
 */
public interface MessageSignature extends Writable {

    /**
     * @return timestamp of the message
     */
    Instant timestamp();

    /**
     * @return salt of the message
     */
    long salt();

    /**
     * @return encoded signature of the message
     */
    byte[] signature();

    /**
     * Writes the message signature into a buffer.
     * @param buf buffer to write into
     */
    default void write(ServerBuffer buf) {
        buf.writeSignature(this);
    }

}
