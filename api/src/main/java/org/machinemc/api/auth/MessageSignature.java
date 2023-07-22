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

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

/**
 * Signature of a chat message.
 * @param timestamp timestamp of the message
 * @param salt salt
 * @param signature signature
 */
public record MessageSignature(Instant timestamp, long salt, byte[] signature) implements Writable {

    public MessageSignature {
        Objects.requireNonNull(timestamp, "Timestamp can not be null");
        Objects.requireNonNull(signature, "Signature can not be null");
    }

    @Override
    public String toString() {
        return "MessageSignature("
                + "timestamp=" + timestamp
                + ", salt=" + salt
                + ", signature=" + Arrays.toString(signature)
                + ')';
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeSignature(this);
    }

}
