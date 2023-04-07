package org.machinemc.server.chat;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
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

import static org.machinemc.server.chat.ChatType.Element.DEFAULT_NARRATION_ELEMENT;

/**
 * Different chat message types, used by Minecraft's chat system.
 */
@AllArgsConstructor
public enum ChatType implements NBTSerializable {

    CHAT(
            NamespacedKey.minecraft("chat"),
            Element.chat(
                    Set.of(Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.text",
                    null,
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    SAY_COMMAND(
            NamespacedKey.minecraft("say_command"),
            Element.chat(
                    Set.of(Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.announcement",
                    null,
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    MSG_COMMAND_INCOMING(
            NamespacedKey.minecraft("msg_command_incoming"),
            Element.chat(
                    Set.of(Parameter.SENDER, Parameter.CONTENT),
                    "commands.message.display.incoming",
                    new TextFormat(ChatColor.GRAY, ChatStyle.ITALIC),
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    MSG_COMMAND_OUTGOING(
            NamespacedKey.minecraft("msg_command_outgoing"),
            Element.chat(
                    Set.of(Parameter.TARGET, Parameter.CONTENT),
                    "commands.message.display.outgoing",
                    new TextFormat(ChatColor.GRAY, ChatStyle.ITALIC),
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    TEAM_MSG_COMMAND_INCOMING(
            NamespacedKey.minecraft("team_msg_command_incoming"),
            Element.chat(
                    Set.of(Parameter.TARGET, Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.team.text",
                    null,
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    TEAM_MSG_COMMAND_OUTGOING(
            NamespacedKey.minecraft("team_msg_command_outgoing"),
            Element.chat(
                    Set.of(Parameter.TARGET, Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.team.sent",
                    null,
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    EMOTE_COMMAND(
            NamespacedKey.minecraft("emote_command"),
            Element.chat(
                    Set.of(Parameter.SENDER, Parameter.TARGET),
                    "chat.type.emote",
                    null,
                    null
            ),
            Element.narration(
                    Set.of(Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.emote",
                    null,
                    null
            )
    ),
    @Deprecated // Is not used by vanilla server?
    TELLRAW(
            NamespacedKey.minecraft("raw"),
            Element.chat(
                    Set.of(Parameter.CONTENT),
                    "%s",
                    null,
                    null
            ),
            Element.narration(
                    Set.of(Parameter.CONTENT),
                    "%s",
                    null,
                    null
            )
    );

    @Getter
    private final NamespacedKey name;
    @Getter(AccessLevel.PROTECTED)
    protected final Element chatElement;
    @Getter(AccessLevel.PROTECTED)
    protected final Element narrationElement;

    public int getId() {
        return ordinal();
    }

    public static ChatType fromID(@Range(from = 0, to = 7) int id) {
        Preconditions.checkArgument(id < values().length, "Unsupported Chat type");
        return values()[id];
    }

    @Override
    public NBTCompound toNBT() {
        NBTCompound element = new NBTCompound(Map.of(
                "chat", chatElement.toNBT(),
                "narration", narrationElement.toNBT()
        ));
        return new NBTCompound(Map.of(
                "name", name.toString(),
                "id", ordinal(),
                "element", element
        ));
    }

    /**
     * Chat and Narration types of chat types, contain information
     * about their parameters, translation key and chat format.
     */
    protected record Element(ElementType type,
                             Set<Parameter> parameters,
                             String translationKey,
                             @Nullable TextFormat format,
                             @Nullable NamespacedKey font) implements NBTSerializable {

        static final Element DEFAULT_NARRATION_ELEMENT = Element.narration(
                Set.of(Parameter.SENDER, Parameter.CONTENT),
                "chat.type.text.narrate",
                null,
                null);

        /**
         * Creates new element of type chat.
         * @param parameters parameters of the element
         * @param translationKey translation key of the element
         * @param format chat format of the element
         * @return created chat type element
         */
        public static Element chat(Set<Parameter> parameters, String translationKey, @Nullable TextFormat format, @Nullable NamespacedKey font) {
            return new Element(ElementType.CHAT, parameters, translationKey, format, font);
        }
        /**
         * Creates new element of type narration.
         * @param parameters parameters of the element
         * @param translationKey translation key of the element
         * @param format chat format of the element
         * @return created chat type element
         */
        public static Element narration(Set<Parameter> parameters, String translationKey, @Nullable TextFormat format, @Nullable NamespacedKey font) {
            return new Element(ElementType.NARRATION, parameters, translationKey, format, font);
        }

        @Override
        public NBTCompound toNBT() {
            final NBTList parameters = new NBTList(this.parameters.stream().map(Parameter::getName).toList());
            final Map<String, String> styleMap = new HashMap<>();
            if(format != null) {
                Map<ChatStyle, Boolean> styles = format.getStyles();
                for (Map.Entry<ChatStyle, Boolean> entry : styles.entrySet()) {
                    if (entry.getValue() != null)
                        styleMap.put(entry.getKey().name().toLowerCase(Locale.ENGLISH), entry.getValue().toString());
                }
                format.getColor().ifPresent(color -> styleMap.put("color", color.getName()));
                if (font != null)
                    styleMap.put("font", font.toString());
            }
            NBTCompound style = new NBTCompound();
            for(String key : styleMap.keySet())
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
