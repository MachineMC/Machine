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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

/**
 * Represents last seen messages by the player.
 * @param entries signatures of messages
 */
public record LastSeenMessages(List<MessageSignature> entries) {

    public LastSeenMessages {
        Objects.requireNonNull(entries, "Entries can not be null");
        entries = new ArrayList<>(entries);
    }

    public LastSeenMessages(final ServerBuffer buf) {
        this(buf.readList(MessageSignature::new));
    }

    /**
     * @return packed last seen messages
     */
    public Packed pack() {
        return new Packed(entries.stream().map(MessageSignature::pack).toList());
    }

    /**
     * @param cache cache used for packing
     * @return packed last seen messages
     */
    public Packed pack(final @Nullable SignedMessageChain.Cache cache) {
        if (cache == null) return pack();
        return new Packed(entries.stream().map(signature -> signature.pack(cache)).toList());
    }

    /**
     * Packed last seen messages.
     * @param entries packed signatures of messages
     */
    public record Packed(List<MessageSignature.Packed> entries) implements Writable {

        public static final Packed EMPTY = new Packed(List.of());

        public Packed {
            Objects.requireNonNull(entries, "Entries can not be null");
            entries = new ArrayList<>(entries);
        }

        public Packed(final ServerBuffer buf) {
            this(buf.readList(MessageSignature.Packed::new));
        }

        @Override
        public void write(final ServerBuffer buf) {
            buf.writeList(entries, ServerBuffer::write);
        }

    }

    public record Update(int offset, BitSet acknowledged) implements Writable {

        public Update(final ServerBuffer buf) {
            this(buf.readVarInt(), buf.readBitSet(20));
        }

        @Override
        public void write(final ServerBuffer buf) {
            buf.writeVarInt(offset);
            buf.writeBitSet(acknowledged, 20);
        }

    }
}
