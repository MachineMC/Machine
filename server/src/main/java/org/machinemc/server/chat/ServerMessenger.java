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
import lombok.Setter;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.api.Server;
import org.machinemc.api.chat.ChatType;
import org.machinemc.api.chat.Messenger;
import org.machinemc.api.entities.Player;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.server.network.packets.out.play.PacketPlayOutSystemChatMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Default implementation of server's messenger.
 */
public class ServerMessenger implements Messenger {

    protected final AtomicInteger idCounter = new AtomicInteger(0);
    private static final String CODEC_TYPE = "minecraft:chat_type";

    private final Map<Integer, ChatType> chatTypes = new ConcurrentHashMap<>();
    @Getter
    private final Server server;

    @Getter @Setter
    private TranslationComponent cannotSendMessage = TranslationComponent.of("chat.cannotSend").modify()
            .color(ChatColor.RED)
            .finish();

    public ServerMessenger(final Server server) {
        this.server = Objects.requireNonNull(server, "Server can not be null");
    }

    /**
     * Creates messenger with default chat types.
     * @param server server
     * @return new messenger
     */
    public static Messenger createDefault(final Server server) {
        final Messenger messenger = new ServerMessenger(server);
        messenger.addChatType(ServerChatType.chat());
        messenger.addChatType(ServerChatType.sayCommand());
        messenger.addChatType(ServerChatType.msgCommandIncoming());
        messenger.addChatType(ServerChatType.msgCommandOutgoing());
        messenger.addChatType(ServerChatType.teamMsgCommandIncoming());
        messenger.addChatType(ServerChatType.teamMsgCommandOutgoing());
        messenger.addChatType(ServerChatType.emoteCommand());
        messenger.addChatType(ServerChatType.tellraw());
        return messenger;
    }

    @Override
    public void addChatType(final ChatType chatType) {
        Objects.requireNonNull(chatType, "Chat type can not be null");
        if (isRegistered(chatType))
            throw new IllegalArgumentException("Chat type '" + chatType.getName() + "' is already registered");
        chatTypes.put(idCounter.getAndIncrement(), chatType);
    }

    @Override
    public boolean removeChatType(final ChatType chatType) {
        Objects.requireNonNull(chatType, "Chat type can not be null");
        return chatTypes.remove(getChatTypeID(chatType)) == null;
    }

    @Override
    public boolean isRegistered(final ChatType chatType) {
        Objects.requireNonNull(chatType, "Chat type can not be null");
        return chatTypes.containsValue(chatType);
    }

    @Override
    public Optional<ChatType> getChatType(final NamespacedKey name) {
        Objects.requireNonNull(name, "Name of the chat type can not be null");
        for (final ChatType chatType : getChatTypes()) {
            if (!(chatType.getName().equals(name))) continue;
            return Optional.of(chatType);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ChatType> getByID(final int id) {
        return Optional.ofNullable(chatTypes.get(id));
    }

    @Override
    public int getChatTypeID(final ChatType chatType) {
        Objects.requireNonNull(chatType, "Chat type can not be null");
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

    @Override
    public void sendRejectionMessage(final Player player) {
        Objects.requireNonNull(player, "Player can not be null");
        player.sendPacket(new PacketPlayOutSystemChatMessage(cannotSendMessage, false));
    }

    @Override
    public NBTCompound getChatTypeNBT(final ChatType chatType) {
        Objects.requireNonNull(chatType, "Chat type can not be null");
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
        return getChatTypes().stream()
                .map(this::getChatTypeNBT)
                .toList();
    }

    @Override
    public String toString() {
        return "Messenger("
                + Arrays.toString(chatTypes.values().toArray(new ChatType[0]))
                + ')';
    }
}
