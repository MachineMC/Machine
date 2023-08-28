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
import lombok.Synchronized;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents a chain of signed messages seen by client.
 */
public class SignedMessageChain {

    private final int capacity;
    private final ObjectList<LastSeenTrackedEntry> trackedMessages = new ObjectArrayList<>();
    @Nullable
    private MessageSignature lastPendingMessage;

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
        final int i = trackedMessages.size() - capacity;
        if (index >= 0 && index <= i) {
            trackedMessages.removeElements(0, index);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Applies acknowledgment of player messages.
     * @param acknowledgment acknowledgment
     * @return new last seen messages
     */
    @Synchronized
    public Optional<LastSeenMessages> applyUpdate(final LastSeenMessages.Update acknowledgment) {
        if (!applyOffset(acknowledgment.offset()))
            return Optional.empty();
        final List<MessageSignature> signatures = new ArrayList<>(acknowledgment.acknowledged().cardinality());

        if (acknowledgment.acknowledged().length() > capacity)
            return Optional.empty();

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

        return Optional.of(new LastSeenMessages(signatures));
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
            return this.pending ? new LastSeenTrackedEntry(this.signature, false) : this;
        }

    }

}
