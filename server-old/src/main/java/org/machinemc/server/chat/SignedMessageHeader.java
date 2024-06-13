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

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents header of a signed player message.
 * @param sender sender of the message
 * @param index index of the message in current session
 * @param signature signature of the message
 */
public record SignedMessageHeader(UUID sender, int index, @Nullable MessageSignature signature) implements Writable {

    public SignedMessageHeader {
        Objects.requireNonNull(sender, "Sender can not be null");
        Preconditions.checkArgument(index >= 0, "Index can not be negative");
    }

    public SignedMessageHeader(final ServerBuffer buf) {
        this(buf.readUUID(), buf.readVarInt(), buf.readOptional(MessageSignature::new).orElse(null));
    }

    /**
     * Creates new unsigned message header.
     * @return header
     */
    public static SignedMessageHeader unsigned(final UUID sender) {
        return new SignedMessageHeader(sender, 0, null);
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeUUID(sender);
        buf.writeVarInt(index);
        buf.writeOptional(signature, ServerBuffer::write);
    }

}
