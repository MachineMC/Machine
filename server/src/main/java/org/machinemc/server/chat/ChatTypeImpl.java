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

import lombok.Builder;
import lombok.Getter;
import org.machinemc.api.chat.ChatType;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.ChatStyle;
import org.machinemc.scriptive.style.TextFormat;

import java.util.Map;
import java.util.Set;

@Getter
@Builder
@SuppressWarnings("ClassCanBeRecord")
public class ChatTypeImpl implements ChatType {

    private final NamespacedKey name;
    private final Element chatElement, narrationElement;

    private static final ChatTypeImpl.Element DEFAULT_NARRATION_ELEMENT = ChatTypeImpl.Element.narration(
            Set.of(ChatTypeImpl.Parameter.SENDER, ChatTypeImpl.Parameter.CONTENT),
            "chat.type.text.narrate",
            null,
            null);

    /**
     * Creates the default 'chat' chat type.
     * @return default 'chat' chat type
     */
    public static ChatTypeImpl chat() {
        return ChatTypeImpl.builder()
                .name(NamespacedKey.minecraft("chat"))
                .chatElement(Element.chat(
                        Set.of(ChatTypeImpl.Parameter.SENDER, ChatTypeImpl.Parameter.CONTENT),
                        "chat.type.text",
                        null,
                        null
                ))
                .narrationElement(DEFAULT_NARRATION_ELEMENT)
                .build();
    }

    /**
     * Creates the default 'say command' chat type.
     * @return default 'say command' chat type
     */
    public static ChatTypeImpl sayCommand() {
        return ChatTypeImpl.builder()
                .name(NamespacedKey.minecraft("say_command"))
                .chatElement(Element.chat(
                        Set.of(ChatTypeImpl.Parameter.SENDER, ChatTypeImpl.Parameter.CONTENT),
                        "chat.type.announcement",
                        null,
                        null
                ))
                .narrationElement(DEFAULT_NARRATION_ELEMENT)
                .build();
    }

    /**
     * Creates the default 'message command incoming' chat type.
     * @return default 'message command incoming' chat type
     */
    public static ChatTypeImpl msgCommandIncoming() {
        return ChatTypeImpl.builder()
                .name(NamespacedKey.minecraft("msg_command_incoming"))
                .chatElement(Element.chat(
                        Set.of(ChatTypeImpl.Parameter.SENDER, ChatTypeImpl.Parameter.CONTENT),
                        "commands.message.display.incoming",
                        new TextFormat(ChatColor.GRAY, ChatStyle.ITALIC),
                        null
                ))
                .narrationElement(DEFAULT_NARRATION_ELEMENT)
                .build();
    }

    /**
     * Creates the default 'message command outgoing' chat type.
     * @return default 'message command outgoing' chat type
     */
    public static ChatTypeImpl msgCommandOutgoing() {
        return ChatTypeImpl.builder()
                .name(NamespacedKey.minecraft("msg_command_outgoing"))
                .chatElement(Element.chat(
                        Set.of(ChatTypeImpl.Parameter.TARGET, ChatTypeImpl.Parameter.CONTENT),
                        "commands.message.display.outgoing",
                        new TextFormat(ChatColor.GRAY, ChatStyle.ITALIC),
                        null
                ))
                .narrationElement(DEFAULT_NARRATION_ELEMENT)
                .build();
    }

    /**
     * Creates the default 'team message command incoming' chat type.
     * @return default 'team message command incoming' chat type
     */
    public static ChatTypeImpl teamMsgCommandIncoming() {
        return ChatTypeImpl.builder()
                .name(NamespacedKey.minecraft("team_msg_command_incoming"))
                .chatElement(Element.chat(
                        Set.of(
                                ChatTypeImpl.Parameter.TARGET,
                                ChatTypeImpl.Parameter.SENDER,
                                ChatTypeImpl.Parameter.CONTENT
                        ),
                        "chat.type.team.text",
                        null,
                        null
                ))
                .narrationElement(DEFAULT_NARRATION_ELEMENT)
                .build();
    }

    /**
     * Creates the default 'team message command outgoing' chat type.
     * @return default 'team message command outgoing' chat type
     */
    public static ChatTypeImpl teamMsgCommandOutgoing() {
        return ChatTypeImpl.builder()
                .name(NamespacedKey.minecraft("team_msg_command_outgoing"))
                .chatElement(Element.chat(
                        Set.of(ChatTypeImpl.Parameter.TARGET,
                                ChatTypeImpl.Parameter.SENDER,
                                ChatTypeImpl.Parameter.CONTENT
                        ),
                        "chat.type.team.sent",
                        null,
                        null
                ))
                .narrationElement(DEFAULT_NARRATION_ELEMENT)
                .build();
    }

    /**
     * Creates the default 'emote command' chat type.
     * @return default 'emote command' chat type
     */
    public static ChatTypeImpl emoteCommand() {
        return ChatTypeImpl.builder()
                .name(NamespacedKey.minecraft("emote_command"))
                .chatElement(Element.chat(
                        Set.of(ChatTypeImpl.Parameter.SENDER, ChatTypeImpl.Parameter.TARGET),
                        "chat.type.emote",
                        null,
                        null
                ))
                .narrationElement(Element.narration(
                        Set.of(ChatTypeImpl.Parameter.SENDER, ChatTypeImpl.Parameter.CONTENT),
                        "chat.type.emote",
                        null,
                        null
                ))
                .build();
    }

    /**
     * Creates the default 'tellraw' chat type.
     * @return default 'tellraw' chat type
     */
    public static ChatTypeImpl tellraw() {
        return ChatTypeImpl.builder()
                .name(NamespacedKey.minecraft("raw"))
                .chatElement(Element.chat(
                        Set.of(ChatTypeImpl.Parameter.CONTENT),
                        "%s",
                        null,
                        null
                ))
                .narrationElement(Element.narration(
                        Set.of(ChatTypeImpl.Parameter.CONTENT),
                        "%s",
                        null,
                        null
                ))
                .build();
    }

    @Override
    public NBTCompound toNBT() {
        return new NBTCompound(Map.of(
                "chat", chatElement.toNBT(),
                "narration", narrationElement.toNBT()
        ));
    }

}
