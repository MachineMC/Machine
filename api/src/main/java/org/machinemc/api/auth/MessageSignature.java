package org.machinemc.api.auth;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * Represents a signature of a message for new 1.19 chat system.
 */
public interface MessageSignature extends Writable {

    /**
     * @return timestamp of the message
     */
    @NotNull Instant timestamp();

    /**
     * @return salt of the message
     */
    long salt();

    /**
     * @return encoded signature of the message
     */
    byte @NotNull [] signature();

    default void write(@NotNull ServerBuffer buf) {
        buf.writeSignature(this);
    }

}
