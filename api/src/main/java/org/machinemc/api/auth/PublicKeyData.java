package org.machinemc.api.auth;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;

import java.security.PublicKey;
import java.time.Instant;

/**
 * Public key data used by Minecraft's auth system.
 */
public interface PublicKeyData extends Writable {

    /**
     * @return public key the client received from Mojang
     */
    PublicKey publicKey();

    /**
     * @return bytes of the public key signature the client received from Mojang
     */
    byte[] signature();

    /**
     * @return when the key data will expire
     */
    Instant timestamp();

    /**
     * Writes the public key data in to a buffer.
     * @param buf buffer to write into
     */
    default void write(ServerBuffer buf) {
        buf.writePublicKey(this);
    }

}
