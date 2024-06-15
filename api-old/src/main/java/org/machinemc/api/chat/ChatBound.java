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
import org.machinemc.api.utils.Writable;
import org.machinemc.scriptive.serialization.ComponentProperties;

import java.util.Optional;

/**
 * Represents a chat type with message participants.
 */
public interface ChatBound extends Writable {

    /**
     * Returns chat type of this chat bound for given messenger.
     * @param messenger messenger
     * @return chat type
     */
    Optional<ChatType> getChatType(Messenger messenger);

    /**
     * Sets new chat type of this chat bound using given messenger.
     * @param chatType chat type
     * @param messenger messenger
     */
    void setChatType(ChatType chatType, Messenger messenger);

    /**
     * @return source of the message
     */
    ComponentProperties getSource();

    /**
     * @param component new source of the message
     */
    void setSource(ComponentProperties component);

    /**
     * @return target of the message
     */
    Optional<ComponentProperties> getTarget();

    /**
     * @param target new target of the message
     */
    void setTarget(@Nullable ComponentProperties target);

}
