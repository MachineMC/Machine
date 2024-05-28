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

import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.ChatBound;
import org.machinemc.api.chat.FilterType;
import org.machinemc.api.chat.PlayerMessage;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.scriptive.serialization.ComponentProperties;

import java.time.Instant;
import java.util.*;

@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayerChatMessage implements PlayerMessage {

    private final SignedMessageHeader header;
    private final SignedMessageBody body;

    @With(AccessLevel.PRIVATE)
    private final List<MessageSignature.Packed> chain;

    private @Nullable ComponentProperties unsignedPart;

    @Getter @Setter
    private FilterType filterType;
    @Setter
    private @Nullable BitSet filteredBits;

    private final ChatBound bound;

    /**
     * Creates unsigned player chat message.
     * @param sender sender of the message
     * @param content content of the message
     * @param chatBound chat bound
     * @return unsigned player message
     */
    public static PlayerChatMessage unsigned(final UUID sender,
                                             final String content,
                                             final ChatBound chatBound) {
        return new PlayerChatMessage(SignedMessageHeader.unsigned(sender), SignedMessageBody.unsigned(content),
                LastSeenMessages.Packed.EMPTY.entries(), null, FilterType.PASS_THROUGH, null, chatBound);
    }

    public PlayerChatMessage(final ServerBuffer buf) {
        header = new SignedMessageHeader(buf);
        body = new SignedMessageBody(buf);
        chain = buf.readList(MessageSignature.Packed::new);
        unsignedPart = buf.readOptional(ServerBuffer::readComponent).orElse(null);
        filterType = FilterType.fromID(buf.readVarInt());
        if (filterType == FilterType.PARTIALLY_FILTERED)
            filteredBits = buf.readBitSet();
        bound = new ServerChatBound(buf);
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.write(header)
                .write(body)
                .writeList(chain, ServerBuffer::write)
                .writeOptional(unsignedPart, ServerBuffer::writeComponent)
                .writeVarInt(filterType.getID());
        if (filterType == FilterType.PARTIALLY_FILTERED)
            filteredBits = buf.readBitSet();
        buf.write(bound);
    }

    @Override
    public UUID getSender() {
        return header.sender();
    }

    @Override
    public int messageID() {
        return header.index();
    }

    @Override
    public Optional<byte[]> getSignature() {
        if (header.signature() == null) return Optional.empty();
        return Optional.of(header.signature().signature());
    }

    @Override
    public String getMessage() {
        return body.content();
    }

    @Override
    public Instant getTimestamp() {
        return body.timestamp();
    }

    @Override
    public Optional<ComponentProperties> getUnsignedContent() {
        return Optional.ofNullable(unsignedPart);
    }

    @Override
    public void setUnsignedContent(final @Nullable ComponentProperties content) {
        unsignedPart = content;
    }

    @Override
    public Optional<BitSet> getFilteredBits() {
        return Optional.ofNullable(filteredBits);
    }

    @Override
    public ChatBound getChatBound() {
        return bound;
    }

    /**
     * Creates copy of given chat message with signatures of lastly seen messages reduced
     * in size using given cache.
     * @param cache cache
     * @return reduced copy of this message
     */
    public PlayerChatMessage pack(final SignedMessageChain.Cache cache) {
        final List<MessageSignature> signatures = new ArrayList<>();
        for (final MessageSignature.Packed packed : chain) {
            if (packed.id() != -1 || packed.signature() == null)
                throw new UnsupportedOperationException("This player message has been already reduced");
            signatures.add(packed.signature());
        }
        return withChain(new LastSeenMessages(signatures).pack(cache).entries());
    }

}
