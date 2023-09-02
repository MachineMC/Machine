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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.machinemc.api.auth.PublicKeyData;
import org.machinemc.api.chat.ChatSession;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a player chat session.
 */
@RequiredArgsConstructor
public class ServerChatSession implements ChatSession {

    private final UUID uuid;
    @Getter
    private final PublicKeyData data;

    private final AtomicInteger messageCounter = new AtomicInteger(0);

    /**
     * @return id of this session
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @return next message index
     */
    public int nextIndex() {
        return messageCounter.getAndIncrement();
    }

}
