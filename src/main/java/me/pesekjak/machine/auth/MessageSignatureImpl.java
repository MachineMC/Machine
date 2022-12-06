package me.pesekjak.machine.auth;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Arrays;

/**
 * Message signature of new 1.19 chat system.
 */
public record MessageSignatureImpl(@NotNull Instant timestamp, long salt, byte @NotNull [] signature) implements MessageSignature {

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "MessageSignature(" +
                "timestamp=" + timestamp +
                ", salt=" + salt +
                ", signature=" + Arrays.toString(signature) +
                ')';
    }

}
