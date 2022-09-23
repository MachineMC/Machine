package me.pesekjak.machine.chat;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pesekjak.machine.nbt.NBTSerializable;
import me.pesekjak.machine.utils.NamespacedKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTString;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.*;

import static me.pesekjak.machine.chat.ChatType.Element.DEFAULT_NARRATION_ELEMENT;

@AllArgsConstructor
public enum ChatType implements NBTSerializable {

    CHAT(
            NamespacedKey.minecraft("chat"),
            Element.chat(
                    Set.of(Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.text",
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    SAY_COMMAND(
            NamespacedKey.minecraft("say_command"),
            Element.chat(
                    Set.of(Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.announcement",
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    MSG_COMMAND_INCOMING(
            NamespacedKey.minecraft("msg_command_incoming"),
            Element.chat(
                    Set.of(Parameter.SENDER, Parameter.CONTENT),
                    "commands.message.display.incoming",
                    Style.style()
                    .color(NamedTextColor.GRAY)
                    .decorate(TextDecoration.ITALIC)
                    .build()
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    MSG_COMMAND_OUTGOING(
            NamespacedKey.minecraft("msg_command_outgoing"),
            Element.chat(
                    Set.of(Parameter.TARGET, Parameter.CONTENT),
                    "commands.message.display.outgoing",
                    Style.style()
                    .color(NamedTextColor.GRAY)
                    .decorate(TextDecoration.ITALIC)
                    .build()
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    TEAM_MSG_COMMAND_INCOMING(
            NamespacedKey.minecraft("team_msg_command_incoming"),
            Element.chat(
                    Set.of(Parameter.TARGET, Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.team.text",
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    TEAM_MSG_COMMAND_OUTGOING(
            NamespacedKey.minecraft("team_msg_command_outgoing"),
            Element.chat(
                    Set.of(Parameter.TARGET, Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.team.sent",
                    null
            ),
            DEFAULT_NARRATION_ELEMENT
    ),
    EMOTE_COMMAND(
            NamespacedKey.minecraft("emote_command"),
            Element.chat(
                    Set.of(Parameter.SENDER, Parameter.TARGET),
                    "chat.type.emote",
                    null
            ),
            Element.narration(
                    Set.of(Parameter.SENDER, Parameter.CONTENT),
                    "chat.type.emote",
                    null
            )
    ),
    @Deprecated // Is not used by vanilla server?
    TELLRAW(
            NamespacedKey.minecraft("raw"),
            Element.chat(
                    Set.of(Parameter.CONTENT),
                    "%s",
                    null
            ),
            Element.narration(
                    Set.of(Parameter.CONTENT),
                    "%s",
                    null
            )
    );

    @Getter @NotNull
    private final NamespacedKey name;
    @Getter(AccessLevel.PROTECTED) @NotNull
    protected final Element chatElement;
    @Getter(AccessLevel.PROTECTED) @NotNull
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
        return NBT.Compound(chatType -> {
            chatType.setString("name", name.toString());
            chatType.setInt("id", ordinal());
            chatType.set("element",
                    NBT.Compound(element -> {
                        element.set("chat", chatElement.toNBT());
                        element.set("narration", narrationElement.toNBT());
                    })
            );
        });
    }

    protected record Element(ElementType type,
                             Set<Parameter> parameters,
                             String translationKey,
                             @Nullable Style style) implements NBTSerializable {

        static final Element DEFAULT_NARRATION_ELEMENT = Element.narration(
                Set.of(Parameter.SENDER, Parameter.CONTENT),
                "chat.type.text.narrate",
                null);

        public static Element chat(Set<Parameter> parameters, String translationKey, @Nullable Style style) {
            return new Element(ElementType.CHAT, parameters, translationKey, style);
        }
        public static Element narration(Set<Parameter> parameters, String translationKey, @Nullable Style style) {
            return new Element(ElementType.NARRATION, parameters, translationKey, style);
        }

        @Override
        public NBTCompound toNBT() {
            final List<NBTString> parameters = new ArrayList<>();
            for(Parameter parameter : this.parameters)
                parameters.add(new NBTString(parameter.getName()));
            final Map<String, String> styleMap = new HashMap<>();
            if(style != null) {
                Map<TextDecoration, TextDecoration.State> decorations = style.decorations();
                for (TextDecoration decoration : decorations.keySet()) {
                    if (decorations.get(decoration) != TextDecoration.State.NOT_SET)
                        styleMap.put(decoration.toString(), decorations.get(decoration).toString());
                }
                TextColor color = style.color();
                if (color != null) {
                    NamedTextColor named = NamedTextColor.namedColor(color.value());
                    if (named != null)
                        styleMap.put("color", named.toString());
                    else
                        styleMap.put("color", color.asHexString());
                }
                Key font = style.font();
                if (font != null)
                    styleMap.put("font", font.asString());
            }
            return NBT.Compound(element -> {
                element.setString("translation_key", translationKey);
                element.set("parameters", NBT.List(
                        NBTType.TAG_String,
                        parameters
                ));
                element.set("style", NBT.Compound(style -> {
                    for(String key : styleMap.keySet())
                        style.setString(key, styleMap.get(key));
                }));
            });
        }

    }

    @AllArgsConstructor
    protected enum ElementType {
        CHAT("chat"),
        NARRATION("narration");
        @Getter
        private final String name;
    }

    @AllArgsConstructor
    protected enum Parameter {
        SENDER("sender"),
        TARGET("target"),
        CONTENT("content");
        @Getter
        private final String name;
    }

}
