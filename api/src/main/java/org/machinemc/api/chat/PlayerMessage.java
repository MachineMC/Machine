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

import org.jetbrains.annotations.Nullable;
import org.machinemc.api.Server;
import org.machinemc.api.utils.Writable;
import org.machinemc.scriptive.serialization.ComponentProperties;

import java.time.Instant;
import java.util.BitSet;
import java.util.Optional;
import java.util.UUID;

public interface PlayerMessage extends Writable {

    /**
     * @return uuid of the sender
     */
    UUID getSender();

    /**
     * @return id of the message
     */
    int messageID();

    /**
     * @return message signature
     */
    Optional<byte[]> getSignature();

    /**
     * @return content of the message
     */
    String getMessage();

    /**
     * @return timestamp of the message
     */
    Instant getTimestamp();

    /**
     * @return extra message content
     */
    Optional<ComponentProperties> getUnsignedContent();

    /**
     * Updates unsigned content of the message.
     * @param content new content
     */
    void setUnsignedContent(@Nullable ComponentProperties content);

    /**
     * @return filter type of the message
     */
    FilterType getFilterType();

    /**
     * Updates filter type of the message.
     * @param filterType new filter type
     */
    void setFilterType(FilterType filterType);

    /**
     * @return filtered content if the filter type is {@link FilterType#PARTIALLY_FILTERED}
     */
    Optional<BitSet> getFilteredBits();

    /**
     * Sets filtered content.
     * @param bitSet filtered bits
     */
    void setFilteredBits(@Nullable BitSet bitSet);

    /**
     * @return bound of the message
     */
    ChatBound getChatBound();

    /**
     * @param messenger messenger to get the id from
     * @return chat type used for this message
     */
    default Optional<ChatType> getChatType(Messenger messenger) {
        return getChatBound().getChatType(messenger);
    }

    /**
     * @param server server
     * @return chat type used for this message
     */
    default Optional<ChatType> getChatType(Server server) {
        return getChatType(server.getMessenger());
    }

    /**
     * @return type of the message
     */
    default MessageType getMessageType() {
        return MessageType.CHAT;
    }

}
