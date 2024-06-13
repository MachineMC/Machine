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
package org.machinemc.server.chat;

import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents body of a signed message.
 * @param content content of the message
 * @param timestamp timestamp of the message
 * @param salt salt of the message
 */
public record SignedMessageBody(String content, Instant timestamp, long salt) implements Writable {

    public SignedMessageBody {
        if (content.length() > 256)
            throw new IllegalArgumentException("Message content too long");
        Objects.requireNonNull(timestamp, "Timestamp of a message body can not be null");
    }

    public SignedMessageBody(final ServerBuffer buf) {
        this(buf.readString(StandardCharsets.UTF_8), buf.readInstant(), buf.readLong());
    }

    /**
     * Creates new unsigned message body.
     * @param content content of the message
     * @return body
     */
    public static SignedMessageBody unsigned(final String content) {
        return new SignedMessageBody(content, Instant.now(), 0L);
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeString(content, StandardCharsets.UTF_8);
        buf.writeInstant(timestamp);
        buf.writeLong(salt);
    }

}
