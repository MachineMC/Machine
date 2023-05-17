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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.machinemc.api.chat.ChatType;
import org.machinemc.api.server.NBTSerializable;
import org.machinemc.api.utils.NamespacedKey;
import org.machinemc.nbt.NBTCompound;
import org.machinemc.nbt.NBTList;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.ChatStyle;
import org.machinemc.scriptive.style.TextFormat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.machinemc.server.chat.ChatTypeImpl.Element.DEFAULT_NARRATION_ELEMENT;

@Builder
@Getter
public class ChatTypeImpl implements ChatType {

    private final NamespacedKey name;
    @Getter(AccessLevel.PROTECTED)
    protected final Element chatElement, narrationElement;

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
                "chat", chatElement,
                "narration", narrationElement
        ));
    }

    /**
     * Chat and Narration types of chat types, contain information
     * about their parameters, translation key and chat format.
     * @param type type of the element
     * @param parameters parameters of the element
     * @param translationKey translation key of the element
     * @param format format of the element
     * @param font font of the element
     */
    protected record Element(ChatTypeImpl.ElementType type,
                             Set<ChatTypeImpl.Parameter> parameters,
                             String translationKey,
                             @Nullable TextFormat format,
                             @Nullable NamespacedKey font) implements NBTSerializable {

        static final ChatTypeImpl.Element DEFAULT_NARRATION_ELEMENT = ChatTypeImpl.Element.narration(
                Set.of(ChatTypeImpl.Parameter.SENDER, ChatTypeImpl.Parameter.CONTENT),
                "chat.type.text.narrate",
                null,
                null);

        /**
         * Creates new element of type chat.
         * @param parameters parameters of the element
         * @param translationKey translation key of the element
         * @param format chat format of the element
         * @param font font of the element
         * @return created chat type element
         */
        public static ChatTypeImpl.Element chat(final Set<ChatTypeImpl.Parameter> parameters,
                                                final String translationKey,
                                                final @Nullable TextFormat format,
                                                final @Nullable NamespacedKey font) {
            return new ChatTypeImpl.Element(ChatTypeImpl.ElementType.CHAT, parameters, translationKey, format, font);
        }
        /**
         * Creates new element of type narration.
         * @param parameters parameters of the element
         * @param translationKey translation key of the element
         * @param format chat format of the element
         * @param font font of the element
         * @return created chat type element
         */
        public static ChatTypeImpl.Element narration(final Set<ChatTypeImpl.Parameter> parameters,
                                                     final String translationKey,
                                                     final @Nullable TextFormat format,
                                                     final @Nullable NamespacedKey font) {
            return new ChatTypeImpl.Element(
                    ChatTypeImpl.ElementType.NARRATION,
                    parameters,
                    translationKey,
                    format,
                    font
            );
        }

        @Override
        public NBTCompound toNBT() {
            final NBTList parameters = new NBTList(this.parameters.stream()
                    .map(ChatTypeImpl.Parameter::getName)
                    .toList());
            final Map<String, String> styleMap = new HashMap<>();
            if (format != null) {
                final Map<ChatStyle, Boolean> styles = format.getStyles();
                for (final Map.Entry<ChatStyle, Boolean> entry : styles.entrySet()) {
                    if (entry.getValue() != null)
                        styleMap.put(entry.getKey().name().toLowerCase(Locale.ENGLISH), entry.getValue().toString());
                }
                format.getColor().ifPresent(color -> styleMap.put("color", color.getName()));
                if (font != null)
                    styleMap.put("font", font.toString());
            }
            final NBTCompound style = new NBTCompound();
            for (final String key : styleMap.keySet())
                style.set(key, styleMap.get(key));
            return new NBTCompound(Map.of(
                    "translation_key", translationKey,
                    "parameters", parameters,
                    "style", style
            ));
        }

    }

    /**
     * Type of chat type element.
     */
    @AllArgsConstructor
    protected enum ElementType {
        CHAT("chat"),
        NARRATION("narration");

        @Getter
        private final String name;
    }

    /**
     * Parameters used by chat type elements.
     */
    @AllArgsConstructor
    protected enum Parameter {
        SENDER("sender"),
        TARGET("target"),
        CONTENT("content");

        @Getter
        private final String name;
    }

}
