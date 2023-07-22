/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.api.auth;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

/**
 * Public key data used by Minecraft's auth system.
 * @param publicKey public key
 * @param signature signature
 * @param timestamp timestamp of the public key
 */
public record PublicKeyData(PublicKey publicKey, byte[] signature, Instant timestamp) implements Writable {

    public PublicKeyData {
        Objects.requireNonNull(publicKey, "Public key can not be null");
        Objects.requireNonNull(signature, "Signature can not be null");
        Objects.requireNonNull(timestamp, "Timestamp can not be null");
    }

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

    /**
     * Writes the public key data in to a buffer.
     * @param buf buffer to write into
     */
    @Override
    public void write(final ServerBuffer buf) {
        Objects.requireNonNull(buf);
        buf.writePublicKey(this);
    }

}
