package me.pesekjak.machine.auth;

import me.pesekjak.machine.utils.FriendlyByteBuf;
import me.pesekjak.machine.utils.ServerBuffer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Arrays;

/**
 * Message signature of new 1.19 chat system.
 */
public record MessageSignatureImpl(@NotNull Instant timestamp, long salt, byte @NotNull [] signature) implements MessageSignature {

    @Override
    public void write(@NotNull ServerBuffer buf) {
        buf.writeInstant(timestamp)
                .writeLong(salt)
                .writeByteArray(signature);
    }

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
