package org.machinemc.server.auth;

import org.machinemc.api.auth.PublicKeyData;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;

/**
 * Default implementation of public key data.
 * @param publicKey public key
 * @param signature signature
 * @param timestamp timestamp of the public key
 */
public record PublicKeyDataImpl(PublicKey publicKey, byte[] signature, Instant timestamp) implements PublicKeyData {

    /**
     * @return true if data are expired
     */
    public boolean hasExpired() {
        return timestamp.isBefore(Instant.now());
    }

    @Override
    public String toString() {
        return "PublicKeyData("
                + "publicKey=" + publicKey
                + ", signature=" + Arrays.toString(signature)
                + ", timestamp=" + timestamp
                + ')';
    }

}
