package org.machinemc.server.auth;

import org.jetbrains.annotations.NotNull;
import org.machinemc.api.auth.MessageSignature;

import java.time.Instant;
import java.util.Arrays;

/**
 * Default implementation of the message signature
 */
public record MessageSignatureImpl(@NotNull Instant timestamp, long salt, byte @NotNull [] signature) implements MessageSignature {

    @Override
    public @NotNull String toString() {
        return "MessageSignature(" +
                "timestamp=" + timestamp +
                ", salt=" + salt +
                ", signature=" + Arrays.toString(signature) +
                ')';
    }

}
