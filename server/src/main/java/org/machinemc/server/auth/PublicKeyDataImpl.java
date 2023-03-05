package org.machinemc.server.auth;

import org.jetbrains.annotations.NotNull;
import org.machinemc.api.auth.PublicKeyData;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;

/**
 * Default implementation of public key data.
 */
public record PublicKeyDataImpl(@NotNull PublicKey publicKey, byte @NotNull [] signature, @NotNull Instant timestamp) implements PublicKeyData {

    /**
     * @return true if data are expired
     */
    public boolean hasExpired() {
        return timestamp.isBefore(Instant.now());
    }

    @Override
    public @NotNull String toString() {
        return "PublicKeyData(" +
                "publicKey=" + publicKey +
                ", signature=" + Arrays.toString(signature) +
                ", timestamp=" + timestamp +
                ')';
    }

}
