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
