package me.pesekjak.machine.auth;

import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;

/**
 * The public key the client received from Mojang.
 */
public record PublicKeyDataImpl(PublicKey publicKey, byte[] signature, Instant timestamp) implements PublicKeyData {

    /**
     * @return true if data are expired
     */
    public boolean hasExpired() {
        return timestamp.isBefore(Instant.now());
    }

    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeLong(timestamp.toEpochMilli())
                .writeByteArray(publicKey.getEncoded())
                .writeByteArray(signature);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "PublicKeyData(" +
                "publicKey=" + publicKey +
                ", signature=" + Arrays.toString(signature) +
                ", timestamp=" + timestamp +
                ')';
    }

}
