package me.pesekjak.machine.auth;

import me.pesekjak.machine.utils.FriendlyByteBuf;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;

public record PublicKeyData(PublicKey publicKey, byte[] signature, Instant timestamp) {

    public boolean hasExpired() {
        return timestamp.isBefore(Instant.now());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeLong(timestamp.toEpochMilli())
                .writeByteArray(publicKey.getEncoded())
                .writeByteArray(signature);
    }

    @Override
    public String toString() {
        return "PublicKeyData{" +
                "publicKey=" + publicKey +
                ", signature=" + Arrays.toString(signature) +
                ", timestamp=" + timestamp +
                '}';
    }
}
