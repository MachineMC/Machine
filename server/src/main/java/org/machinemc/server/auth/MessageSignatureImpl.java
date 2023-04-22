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
 * If not, see <https://www.gnu.org/licenses/>.
 */
package org.machinemc.server.auth;

import org.machinemc.api.auth.MessageSignature;

import java.time.Instant;
import java.util.Arrays;

/**
 * Default implementation of the message signature.
 * @param timestamp timestamp of the message
 * @param salt salt
 * @param signature signature
 */
public record MessageSignatureImpl(Instant timestamp, long salt, byte[] signature) implements MessageSignature {

    @Override
    public String toString() {
        return "MessageSignature("
                + "timestamp=" + timestamp
                + ", salt=" + salt
                + ", signature=" + Arrays.toString(signature)
                + ')';
    }

}
