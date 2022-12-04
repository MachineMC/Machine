package me.pesekjak.machine.auth;

import me.pesekjak.machine.utils.Writable;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.time.Instant;

/**
 * Public key data used by Minecraft's auth system.
 */
public interface PublicKeyData extends Writable {

    /**
     * @return public key the client received from Mojang
     */
    @NotNull PublicKey publicKey();

    /**
     * @return bytes of the public key signature the client received from Mojang
     */
    byte @NotNull [] signature();

    /**
     * @return when the key data will expire
     */
    @NotNull Instant timestamp();

}
