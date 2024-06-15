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
package org.machinemc.api.chat;

import org.machinemc.api.auth.PublicKeyData;

import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;

public interface ChatSession {

    /**
     * @return id of this session
     */
    UUID getUUID();

    /**
     * @return public key data of this session
     */
    PublicKeyData getData();

    /**
     * @return public key of the session
     */
    default PublicKey getPublicKey() {
        return getData().publicKey();
    }

    /**
     * @return signature of the session
     */
    default byte[] getSignature() {
        return getData().signature();
    }

    /**
     * @return timestamp of the session
     */
    default Instant getTimestamp() {
        return getData().timestamp();
    }

    /**
     * @return whether the session is expired
     */
    default boolean isExpired() {
        return getTimestamp().isBefore(Instant.now());
    }

}
