package org.machinemc.server.auth;

import org.machinemc.api.auth.MessageSignature;

import java.time.Instant;
import java.util.Arrays;

/**
 * Default implementation of the message signature.
 * @param timestamp timestamp of the message
 * @param salt salt
 * @param signature signature
 */
public record MessageSignatureImpl(Instant timestamp, long salt, byte[] signature) implements MessageSignature {

    @Override
    public String toString() {
        return "MessageSignature("
                + "timestamp=" + timestamp
                + ", salt=" + salt
                + ", signature=" + Arrays.toString(signature)
                + ')';
    }

}
