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
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.chat.ChatType;
import org.machinemc.api.chat.MessageType;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.entities.Player;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.server.Machine;
import org.machinemc.server.network.packets.out.play.PacketPlayOutSystemChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Default implementation of server's messenger.
 */
@RequiredArgsConstructor
public class MessengerImpl implements Messenger {

    protected final AtomicInteger idCounter = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:chat_type";

    private final Map<Integer, ChatType> chatTypes = new ConcurrentHashMap<>();
    @Getter
    private final Machine server;

    @Getter @Setter
    private TranslationComponent cannotSendMessage = TranslationComponent.of("chat.cannotSend").modify()
            .color(ChatColor.RED)
            .finish();

    /**
     * Creates messenger with default chat types.
     * @param server server
     * @return new messenger
     */
    public static Messenger createDefault(final Machine server) {
        final Messenger messenger = new MessengerImpl(server);
        messenger.addChatType(ChatTypeImpl.chat());
        messenger.addChatType(ChatTypeImpl.sayCommand());
        messenger.addChatType(ChatTypeImpl.msgCommandIncoming());
        messenger.addChatType(ChatTypeImpl.msgCommandOutgoing());
        messenger.addChatType(ChatTypeImpl.teamMsgCommandIncoming());
        messenger.addChatType(ChatTypeImpl.teamMsgCommandOutgoing());
        messenger.addChatType(ChatTypeImpl.emoteCommand());
        messenger.addChatType(ChatTypeImpl.tellraw());
        return messenger;
    }

    @Override
    public void addChatType(final ChatType chatType) {
        if (isRegistered(chatType))
            throw new IllegalArgumentException("Chat type '" + chatType.getName() + "' is already registered");
        chatTypes.put(idCounter.getAndIncrement(), chatType);
    }

    @Override
    public boolean removeChatType(final ChatType chatType) {
        return chatTypes.remove(getChatTypeID(chatType)) == null;
    }

    @Override
    public boolean isRegistered(final ChatType chatType) {
        return chatTypes.containsValue(chatType);
    }

    @Override
    public @Nullable ChatType getChatType(final NamespacedKey name) {
        for (final ChatType chatType : getChatTypes()) {
            if (!(chatType.getName().equals(name))) continue;
            return chatType;
        }
        return null;
    }

    @Override
    public @Nullable ChatType getByID(final int id) {
        return chatTypes.get(id);
    }

    @Override
    public int getChatTypeID(final ChatType chatType) {
        for (final Map.Entry<Integer, ChatType> entry : chatTypes.entrySet()) {
            if (entry.getValue().equals(chatType))
                return entry.getKey();
        }
        return -1;
    }

    @Override
    public @Unmodifiable Set<ChatType> getChatTypes() {
        return chatTypes.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    // TODO Player Message impl once it's done
    @Override
    public boolean sendMessage(final Player player, final Component message, final MessageType messageType) {
        if (Messenger.accepts(player, messageType)) {
            player.sendPacket(new PacketPlayOutSystemChatMessage(message, false));
            return true;
        }
        return false;
    }

    @Override
    public void sendRejectionMessage(final Player player) {
        player.sendPacket(new PacketPlayOutSystemChatMessage(cannotSendMessage, false));
    }

    @Override
    public NBTCompound getChatTypeNBT(final ChatType chatType) {
        final NBTCompound nbtCompound = chatType.toNBT();
        return new NBTCompound(Map.of(
                "name", chatType.getName().toString(),
                "id", getChatTypeID(chatType),
                "element", nbtCompound
        ));
    }

    @Override
    public String getCodecType() {
        return CODEC_TYPE;
    }

    @Override
    public List<NBTCompound> getCodecElements() {
        return new ArrayList<>(getChatTypes().stream()
                .map(this::getChatTypeNBT)
                .toList());
    }

}
