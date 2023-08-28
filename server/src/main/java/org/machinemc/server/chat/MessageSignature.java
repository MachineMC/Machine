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

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.api.utils.Writable;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a signature of a player message.
 * @param signature signature
 */
public record MessageSignature(byte[] signature) implements Writable {

    public MessageSignature {
        if (signature.length != 256) throw new IllegalArgumentException("Signatures must be 256 bytes long");
    }

    public MessageSignature(final ServerBuffer buf) {
        this(buf.readBytes(256));
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeBytes(signature);
    }

    /**
     * @return signature
     */
    public Optional<byte[]> getSignature() {
        return Optional.ofNullable(signature);
    }

    /**
     * @return packed signature
     */
    public Packed pack() {
        return new Packed(this);
    }

    @Override
    public String toString() {
        return "MessageSignature(" + Arrays.toString(signature) + ")";
    }

    /**
     * Represents packed message signature used for identifying previously seen player messages.
     * @param id message id
     * @param signature signature
     */
    public record Packed(int id, @Nullable MessageSignature signature) implements Writable {

        public Packed {
            if (id == -1 && signature == null) throw new IllegalArgumentException();
        }

        public Packed(final MessageSignature signature) {
            this(-1, signature);
        }

        public Packed(final int id) {
            this(id, null);
        }

        public Packed(final ServerBuffer buf) {
            this(read(buf));
        }

        private Packed(final Packed packed) {
            this(packed.id, packed.signature);
        }

        @Override
        public void write(final ServerBuffer buf) {
            buf.writeVarInt(id + 1);
            if (id == -1) buf.write(signature);
        }

        private static Packed read(final ServerBuffer buf) {
            final int id = buf.readVarInt() - 1;
            return id == -1 ? new MessageSignature.Packed(new MessageSignature(buf)) : new MessageSignature.Packed(id);
        }

    }

}
