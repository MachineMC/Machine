package me.pesekjak.machine.auth;

import me.pesekjak.machine.utils.FriendlyByteBuf;

import java.time.Instant;

public record MessageSignature(Instant timestamp, long salt, byte[] signature) {

    public void write(FriendlyByteBuf buf) {
        buf.writeInstant(timestamp)
                .writeLong(salt)
                .writeByteArray(signature);
    }

}
