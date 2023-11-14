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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Synchronized;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents a chain of signed messages seen by client.
 */
public class SignedMessageChain {

    public static final int DEFAULT_CAPACITY = 20;

    private final int capacity;
    private final ObjectList<LastSeenTrackedEntry> trackedMessages = new ObjectArrayList<>();
    @Nullable
    private MessageSignature lastPendingMessage;

    private LastSeenMessages lastSeenMessages = new LastSeenMessages(Collections.emptyList());
    @Getter
    private final Cache cache = new Cache(Cache.DEFAULT_CAPACITY);

    public SignedMessageChain(final int capacity) {
        Preconditions.checkArgument(capacity > 0, "Message chain can not be smaller than 1");
        this.capacity = capacity;

        for (int i = 0; i < capacity; ++i)
            trackedMessages.add(null);
    }

    /**
     * Inserts new message signature to the chain.
     * @param signature next signature
     */
    @Synchronized
    public void addPending(final MessageSignature signature) {
        cache.cache(signature, lastSeenMessages.entries());
        if (signature.equals(lastPendingMessage)) return;
        trackedMessages.add(new LastSeenTrackedEntry(signature, true));
        lastPendingMessage = signature;
    }

    /**
     * @return amount of tracked messages
     */
    public int getTrackedMessagesCount() {
        return trackedMessages.size();
    }

    /**
     * Applies offset of acknowledged messages.
     * @param index index
     * @return true if the operation was successful
     */
    @Synchronized
    public boolean applyOffset(final int index) {
        if (index < 0) return false;
        if (index > trackedMessages.size() - capacity) return false;
        trackedMessages.removeElements(0, index);
        return true;
    }

    /**
     * Applies acknowledgment of player messages.
     * @param acknowledgment acknowledgment
     * @return new last seen messages
     */
    @Synchronized
    public Optional<LastSeenMessages> applyUpdate(final LastSeenMessages.Update acknowledgment) {
        if (!applyOffset(acknowledgment.offset())) return Optional.empty();
        if (acknowledgment.acknowledged().length() > capacity) return Optional.empty();

        final List<MessageSignature> signatures = new ArrayList<>(acknowledgment.acknowledged().cardinality());

        for (int i = 0; i < capacity; ++i) {
            final boolean acknowledged = acknowledgment.acknowledged().get(i);
            final LastSeenTrackedEntry lastSeenTrackedEntry = trackedMessages.get(i);

            if (acknowledged) {
                if (lastSeenTrackedEntry == null) return Optional.empty();
                trackedMessages.set(i, lastSeenTrackedEntry.acknowledge());
                signatures.add(lastSeenTrackedEntry.signature());
                continue;
            }

            if (lastSeenTrackedEntry != null && !lastSeenTrackedEntry.pending()) return Optional.empty();

            trackedMessages.set(i, null);
        }

        lastSeenMessages = new LastSeenMessages(signatures);
        return Optional.of(lastSeenMessages);
    }

    /**
     * Represents seen chat entry.
     * @param signature signature
     * @param pending whether the entry has been acknowledged by the client
     */
    public record LastSeenTrackedEntry(MessageSignature signature, boolean pending) {

        /**
         * @return acknowledged entry
         */
        public LastSeenTrackedEntry acknowledge() {
            return pending ? new LastSeenTrackedEntry(signature, false) : this;
        }

    }

    /**
     * Represents cached message signatures.
     */
    public static class Cache {

        public static final int DEFAULT_CAPACITY = 128;

        private final MessageSignature[] entries;

        Cache(final int capacity) {
            entries = new MessageSignature[capacity];
        }

        /**
         * Returns index of a message signature in this cache.
         * @param signature signature
         * @return its index
         */
        public int index(final MessageSignature signature) {
            for (int i = 0; i < this.entries.length; ++i) {
                if (!signature.equals(this.entries[i])) continue;
                return i;
            }
            return MessageSignature.Packed.NOT_CACHED;
        }

        /**
         * Caches new signature.
         * @param signature signature to cache
         * @param seen last seen messages
         */
        public void cache(final @Nullable MessageSignature signature, final List<MessageSignature> seen) {
            final ArrayDeque<MessageSignature> arrayDeque = new ArrayDeque<>(seen.size() + 1);
            arrayDeque.addAll(seen);
            if (signature != null) arrayDeque.add(signature);

            final Set<MessageSignature> copy = new ObjectOpenHashSet<>(arrayDeque);

            for (int i = 0; !arrayDeque.isEmpty() && i < this.entries.length; ++i) {
                final MessageSignature messageSignature = this.entries[i];
                this.entries[i] = arrayDeque.removeLast();

                if (messageSignature != null && !copy.contains(messageSignature))
                    arrayDeque.addFirst(messageSignature);
            }
        }

    }

}
