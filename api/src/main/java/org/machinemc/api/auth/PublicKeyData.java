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
