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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.ChatBound;
import org.machinemc.api.chat.ChatType;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.utils.ServerBuffer;
import org.machinemc.scriptive.components.Component;

import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class ServerChatBound implements ChatBound {

    private int chatTypeID;
    private Component source;
    @Getter(AccessLevel.NONE)
    private @Nullable Component target;

    public ServerChatBound(final int chatTypeID, final Component source, final @Nullable Component target) {
        this.chatTypeID = chatTypeID;
        this.source = Objects.requireNonNull(source, "Source can not be null");
        this.target = target;
    }

    public ServerChatBound(final Messenger messenger, final ChatType chatType, final Component name, final Component targetName) {
        this(messenger.getChatTypeID(chatType), name, targetName);
    }

    public ServerChatBound(final ServerBuffer buf) {
        this(buf.readVarInt(), buf.readComponent(), buf.readOptional(ServerBuffer::readComponent).orElse(null));
    }

    @Override
    public Optional<ChatType> getChatType(final Messenger messenger) {
        return messenger.getChatTypes().stream().filter(chatType -> messenger.getChatTypeID(chatType) == chatTypeID).findAny();
    }

    @Override
    public void setChatType(final ChatType chatType, final Messenger messenger) {
        final int id = messenger.getChatTypeID(chatType);
        if (id == -1) throw new RuntimeException("ChatType '" + chatType.getName() + "' is not registered in provided Messenger");
        chatTypeID = id;
    }

    @Override
    public Optional<Component> getTarget() {
        return Optional.ofNullable(target);
    }

    @Override
    public void write(final ServerBuffer buf) {
        buf.writeVarInt(chatTypeID);
        buf.writeComponent(source);
        buf.writeOptional(target, ServerBuffer::writeComponent);
    }

}
