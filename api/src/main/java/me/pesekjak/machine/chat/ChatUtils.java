package me.pesekjak.machine.chat;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Utils for chat operations.
 */
@UtilityClass
public class ChatUtils {

    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder().build();
    public static final char COLOR_CHAR = 167; // §
    private static final char CONSOLE_COLOR_CHAR = '\033';

    private static final Pattern AMP_COLOR_CODE_PATTERN = Pattern.compile("&([\\daAbBcCdDeEfFkKlLmMnNoOrR])");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile(COLOR_CHAR + "([\\daAbBcCdDeEfFkKlLmMnNoOrR])");

    public static final String DEFAULT_CHAT_FORMAT = "<%name%> %message%";

    /**
     * Translates the '&' color symbol to Minecraft's color symbol
     * @param string string to translate colors for
     * @return string with translated color codes
     */
    public static @NotNull String colored(@NotNull String string) {
        return AMP_COLOR_CODE_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1");
    }

    /**
     * Deserializes the serialized chat component
     * @param string serialized chat component to deserialize
     * @return chat component from given string
     */
    public static @NotNull TextComponent stringToComponent(@NotNull String string) {
        return legacyComponentSerializer.deserialize(string);
    }

    /**
     * Serializes the given chat component as string.
     * @param component component to serialize
     * @return serialized component
     */
    public static @NotNull String componentToString(@NotNull Component component) {
        return legacyComponentSerializer.serialize(component);
    }

    /**
     * Formats the string with vanilla Minecraft color codes with ascii
     * terminal colors.
     * @param string string to format
     * @return formatted string for console
     */
    public static @NotNull String consoleFormatted(@NotNull String string) {
        return COLOR_CODE_PATTERN.matcher(colored(string)).replaceAll(matchResult -> {
            final ChatColor color = ChatColor.byChar(matchResult.group(1));
            if (color == null || color.consoleCode < 0)
                return matchResult.group();
            return CONSOLE_COLOR_CHAR + "[" + (color.isColor ? "0;" : "") + color.consoleCode + "m";
        });
    }

    /**
     * Formats the component with ascii terminal colors.
     * @param component component to format
     * @return formatted component as string for console
     */
    public static @NotNull String consoleFormatted(@NotNull Component component) {
        return appendComponent(new StringBuilder(), component).toString();
    }

    @Contract("_, _ -> param1")
    private static @NotNull StringBuilder appendComponent(@NotNull StringBuilder builder, @NotNull Component component) {
        if(component instanceof TextComponent textComponent) {
            builder.append(CONSOLE_COLOR_CHAR + "[").append(ChatColor.RESET.consoleCode).append("m");
            final Style style = textComponent.style();
            final TextColor color = style.color();
            if(color != null)
                builder.append(asciiColor(color));
            for(TextDecoration decoration : style.decorations().keySet()) {
                if(style.decorations().get(decoration) != TextDecoration.State.TRUE) continue;
                builder.append(CONSOLE_COLOR_CHAR + "[");
                switch (decoration) {
                    case BOLD -> builder.append(ChatColor.BOLD.consoleCode);
                    case ITALIC -> builder.append(ChatColor.ITALIC.consoleCode);
                    case OBFUSCATED -> builder.append(ChatColor.OBFUSCATED.consoleCode);
                    case UNDERLINED -> builder.append(ChatColor.UNDERLINED.consoleCode);
                    case STRIKETHROUGH -> builder.append(ChatColor.STRIKETHROUGH.consoleCode);
                }
                builder.append("m");
            }
            builder.append(textComponent.content());
        }
        for(Component child : component.children())
            appendComponent(builder, child);
        return builder;
    }

    /**
     * Converts text color to a ascii string for terminal.
     * @param color color to convert
     * @return color converted to ascii color format
     */
    public static @NotNull String asciiColor(@NotNull TextColor color) {
        return "\u001B[38;2;" + color.red() + ";" + color.green() + ";" + color.blue() + "m";
    }

}
